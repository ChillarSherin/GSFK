package com.chillarcards.gsfk.ui.scan

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.chillarcards.gsfk.MainActivity
import com.chillarcards.gsfk.R
import com.chillarcards.gsfk.databinding.FragmentScanResultBinding
import com.chillarcards.gsfk.interfaces.IAdapterViewUtills
import com.chillarcards.gsfk.ui.adapter.ResultAdapter
import com.chillarcards.gsfk.utills.CommonDBaseModel
import com.chillarcards.gsfk.utills.Const
import com.chillarcards.gsfk.utills.PrefManager
import com.chillarcards.gsfk.utills.Status
import com.chillarcards.gsfk.viewmodel.ScanViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ScanResultFragment : Fragment(), IAdapterViewUtills {

    lateinit var binding: FragmentScanResultBinding
    private var mediaPlayer: MediaPlayer? = null
    private val scanViewModel by viewModel<ScanViewModel>()
    private val args: ScanResultFragmentArgs by navArgs()
    private val idList: MutableList<String> = mutableListOf()
    private var idSele: String = ""
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan_result, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()
        prefManager = PrefManager(requireContext())

        scanViewModel.mobileNumber.value = args.mobileNo
        scanViewModel.token.value = args.token
        scanViewModel.qrScan.value = args.sannedCode
        scanViewModel.getScanResponse()
        setUpObserver()

        Const.enableButton(binding.backBtn)

        binding.backBtn.setOnClickListener{
            findNavController().popBackStack()
        }

        binding.checkinBtn.setOnClickListener{
            scanViewModel.mobileNumber.value = args.mobileNo
            scanViewModel.token.value = args.token
            scanViewModel.qrScan.value = args.sannedCode
            scanViewModel.itemSelId.value = idSele
            scanViewModel.idListSele = getCurrentIds() as MutableList<String>
            scanViewModel.updateScanResponse()
            setUpdateObserver()
            val currentIds =getCurrentIds()
            println("---"+currentIds) // Output: [23]

        }
    }

    private fun setToolbar() {
        binding.toolbar.toolbarBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.toolbarTitle.text = getString(R.string.app_full)
    }

    //API CALL
    private fun setUpObserver() {
        try {
            scanViewModel.scanData.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            hideProgress()
                            it.data?.let { qrData ->
                                if (qrData.status?.code == "200") {
                                    if (qrData.data.isNotEmpty()) {
                                        if (qrData.data[0].full_name == null){
                                            binding.attendeeName.text = "Guest"
                                        }else{
                                            binding.attendeeName.text = qrData.data[0].full_name
                                        }
                                      //  binding.category.text = qrData.data[0].sub_package_name
                                        // Log data for debugging
                                        for (i in qrData.data.indices) {
                                            Log.d("api_scan_res", "Data at index $i: ${qrData.data[i].event_transaction_id}")
                                            Log.d("api_scan_res", "Data at index $i: ${qrData.data[i].sub_package_name}")
                                        }
                                        val transactionAdapter = ResultAdapter(
                                            qrData.data, context,this@ScanResultFragment)

                                        binding.tranRv.adapter = transactionAdapter
                                        binding.tranRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                                    } else {
                                        Log.d("api_scan_res", "Data list is empty")
                                    }
                                }
                                else if (qrData.status?.code == "401") {
                                    showLogoutConfirmationDialog(qrData.status?.message_details.toString())
                                }
                                else if (qrData.status?.code == "204") {
                                    findNavController().popBackStack()
                                    Const.shortToast(
                                        requireContext(),qrData.status?.message_details.toString()
                                    )
                                }else {
                                    Const.shortToast(
                                        requireContext(),qrData.status?.code.toString()
                                    )
                                }
                            }
                        }
                        Status.LOADING -> {
                            showProgress()
                        }
                        Status.ERROR -> {
                            hideProgress()
                            Const.shortToast(requireContext(), it.message.toString())
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("abc_mobile", "setUpObserver: ", e)
        }
    }

    //CHECKIN
    private fun setUpdateObserver() {
        try {
            scanViewModel.scanUpdateData.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            hideProgress()
                            it.data?.let { qrData ->
                                if (qrData.status.code == "200") {

                                    // Initialize MediaPlayer in onCreate or another appropriate method
                                    mediaPlayer = MediaPlayer.create(context, R.raw.bell_audio)
                                    if (!mediaPlayer!!.isPlaying) {
                                        mediaPlayer!!.start()
                                    }
                                    //TODO API CALL to update entry
                                    findNavController().popBackStack()
                                }
                                else if (qrData.status.code == "400") {
                                    Log.d("abc_home", "json_ abc_home $qrData")

                                    showLogoutConfirmationDialog(qrData.status.message_details.toString())

                                }
                                else {
                                    Log.d("abc_home", "json_ dsdasdasd$qrData")
                                    Const.shortToast(
                                        requireContext(), qrData.status.code.toString()
                                    )
                                }
                            }
                        }
                        Status.LOADING -> {
                            showProgress()
                        }
                        Status.ERROR -> {
                            hideProgress()
                            Const.shortToast(requireContext(), it.message.toString())
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("abc_mobile", "setUpObserver: ", e)
        }
    }

    private fun showProgress() {
        binding.loginProgress.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.loginProgress.visibility = View.GONE
    }
    override fun getAdapterPosition(
        Position: Int,
        ValueArray: ArrayList<CommonDBaseModel>,
        Mode: String?
    ) {
        if(Mode.equals("CheckIn")) {
            Const.enableButton(binding.checkinBtn)
            addNewId(ValueArray[0].mastIDs.toString())
            idSele = ValueArray[0].mastIDs.toString()
        }
    }

    private fun addNewId(newId: String) {
        idList.add(newId)
        if (idList.size > 1) {
            val previousId = idList[0]
            idList.remove(previousId)
        }
    }
    private fun getCurrentIds(): List<String> {
        return idList.toList()
    }

    private fun showLogoutConfirmationDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage(message)
        builder.setCancelable(false);
        builder.setPositiveButton("Logout") { dialogInterface, _ ->
            dialogInterface.dismiss()
            logout()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun logout() {

        prefManager.clearAll()
        Const.clearCache(requireContext())
        prefManager.setIsLoggedIn(false)

        Log.d("abc_home", "showLogoutAlert: recreating activity.. all data cleared")
        val intent = Intent(requireContext(), MainActivity::class.java)
        ActivityCompat.finishAffinity(requireActivity())
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

}