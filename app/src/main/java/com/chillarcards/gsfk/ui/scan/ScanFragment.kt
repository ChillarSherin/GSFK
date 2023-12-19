package com.chillarcards.gsfk.ui.scan

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.*
import com.chillarcards.gsfk.MainActivity
import com.chillarcards.gsfk.R
import com.chillarcards.gsfk.databinding.FragmentScanBinding
import com.chillarcards.gsfk.utills.Const
import com.chillarcards.gsfk.utills.PrefManager
import com.google.android.gms.location.*
import java.util.*


class ScanFragment : Fragment() {

    lateinit var binding: FragmentScanBinding
    private lateinit var codeScanner: CodeScanner

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId: Int = 101
    private var lattitude: Double? = null
    private var longitude: Double? = null
    lateinit var prefManager: PrefManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefManager = PrefManager(requireContext())

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) !=
            PackageManager.PERMISSION_GRANTED &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        ) {
            MainActivity.setLoggedInValue(false)

            if (ActivityCompat.shouldShowRequestPermissionRationale( requireActivity(), Manifest.permission.CAMERA )
            ) {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
        else {
            MainActivity.setLoggedInValue(true)
            setUpQRScanner()
        }

    }

    private fun setUpQRScanner() {
        codeScanner = CodeScanner(requireContext(), binding.scannerView)
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            requireActivity().runOnUiThread {
                onScannedComplete(it.text)

            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            requireActivity().runOnUiThread {
                Toast.makeText(
                    requireContext(), "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        binding.uploadBtn.setOnClickListener {
            onUploadCreateDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        if (this@ScanFragment::codeScanner.isInitialized)
            codeScanner.startPreview()
    }

    override fun onPause() {
        if (this@ScanFragment::codeScanner.isInitialized)
            codeScanner.releaseResources()
        super.onPause()
    }

    private fun onScannedComplete(scannedValue: String) {
        Log.d("abc_scan", "onScannedComplete: $scannedValue")
        // TODO: do validations for scanned value if necessary
        try {
            findNavController().navigate(
                ScanFragmentDirections.actionScanFragmentToResultFragment(scannedValue))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @SuppressLint("MissingPermission")
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            Log.d("abc_scan", "reqPerm: $isGranted")
            if (isGranted) {
                setUpQRScanner()
                refreshFragment()
            } else {
                findNavController().popBackStack()
            }
        }

    private fun refreshFragment() {
        try {
            val id = findNavController().currentDestination?.id
            findNavController().popBackStack(id!!, true)
            findNavController().navigate(id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Show alert dialog to request permissions
    private fun showAlert() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Need permission(s)")
        builder.setMessage("Some permissions are required to do the task.")
        builder.setPositiveButton("OK") { _, _ -> goToSettings() }
        builder.setNeutralButton("Cancel") { _, _ -> findNavController().popBackStack() }
        val dialog = builder.create()
        dialog.show()
    }

    private fun goToSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        requireContext().startActivity(intent)
    }

    private fun onUploadCreateDialog() {
        val dialog = activity?.let { it1 -> Dialog(it1) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.popup_barcode)
        val mBarcodeEt = dialog?.findViewById(R.id.mobile_et) as EditText
        val yesBtn = dialog.findViewById(R.id.saveBtn) as Button
        val noBtn = dialog.findViewById(R.id.cancelBtn) as Button
        yesBtn.setOnClickListener {
            onScannedComplete(mBarcodeEt.text.toString())
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }


    //START LAT AND LONG


    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location = task.result
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val list: List<Address> =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>

//                        tvLatitude.text = "Latitude\n${list[0].latitude}"
//                        tvLongitude.text = "Longitude\n${list[0].longitude}"

                    lattitude = location.latitude
                    longitude = location.longitude
//
//                        prefManager.setCrntLat(lattitude.toString())
//                        prefManager.setCrntLong(longitude.toString())

                    if(location == null){
                        requestNewLocationData()
                    }
                }
            }
            else {
                getLocation()

            }

        }
        else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        Log.d("abc_store", "requestPermissions: ")
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d("abc_store", "onRequestPermissionsResult: $requestCode")
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }


    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
        Const.shortToast(
            requireContext(),
            resources.getString(R.string.fetching_location)
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation!!
            lattitude = mLastLocation.latitude
            longitude = mLastLocation.longitude
        }
    }
    //END LAT AND LONG
}