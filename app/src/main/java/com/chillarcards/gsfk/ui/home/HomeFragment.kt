package com.chillarcards.gsfk.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chillarcards.gsfk.MainActivity
import com.chillarcards.gsfk.R
import com.chillarcards.gsfk.databinding.FragmentHomeBinding
import com.chillarcards.gsfk.ui.adapter.ImageSliderAdapter
import com.chillarcards.gsfk.utills.Const
import com.chillarcards.gsfk.utills.Const.Companion.getCurrentDate
import com.chillarcards.gsfk.utills.PrefManager
import com.chillarcards.gsfk.utills.Status
import com.chillarcards.gsfk.viewmodel.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    private val images = listOf(
        R.drawable.home_one,
        R.drawable.home_two,
        R.drawable.home_three,
        R.drawable.home_four
    )
    private lateinit var adapter: ImageSliderAdapter
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private val delay = 2000L // Set your desired delay in milliseconds
    private lateinit var prefManager: PrefManager
    private val reportViewModel by viewModel<ReportViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                alertMsg(requireContext())
            }
        })
    }

    fun alertMsg(context: Context) {
        try {
            PrefManager(context)
            val builder = AlertDialog.Builder(context)
            // Create the AlertDialog

            //set title for alert dialog
            builder.setTitle(R.string.alert_heading)
            //set message for alert dialog
            builder.setMessage(R.string.pop_alert_message)
            builder.setIcon(R.mipmap.ic_launcher)
            builder.setCancelable(false)

            //performing positive action
            builder.setPositiveButton(context.getString(R.string.logout)) { _, _ ->
                logout()
            }
            builder.setNegativeButton(context.getString(R.string.cancel)) { _, _ ->
//                builder.dismiss()
            }
            val alertDialog: AlertDialog = builder.create()

            alertDialog.setCanceledOnTouchOutside(false)
            alertDialog.show()
        } catch (e: Exception) {
            e.toString()
            Log.d("abc_home", "mobile"+e.printStackTrace())

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefManager = PrefManager(requireContext())

        reportViewModel.mobileNumber.value = prefManager.getMobileNo()
        reportViewModel.token.value = prefManager.getToken()
        reportViewModel.date.value = getCurrentDate()
        reportViewModel.getReportResponse()
        setUpObserver()

        adapter = ImageSliderAdapter(images)
        binding.viewPager.adapter = adapter

        binding.scanImg.setAnimation(R.raw.ic_scan)
        binding.reportImg.setAnimation(R.raw.ic_report_menu)

        binding.menuIcon.setOnClickListener {
            openOptionsMenu(it)
        }
        // Start auto-scrolling when the activity is created
        handler.postDelayed(runnable, delay)

        binding.scanCard.setOnClickListener {
            findNavController().navigate(
                 HomeFragmentDirections.actionHomeFragmentToScanFragment(
                     prefManager.getMobileNo(),prefManager.getToken()
                 )
             )
        }

        binding.reportCard.setOnClickListener {
             findNavController().navigate(
                 HomeFragmentDirections.actionHomeFragmentToReportFragment()
             )
        }

    }

    private val runnable = object : Runnable {
        override fun run() {
            if (currentPage == images.size) {
                currentPage = 0
            }
            binding.viewPager.setCurrentItem(currentPage++, true)
            handler.postDelayed(this, delay)
        }
    }


    override fun onStop() {
        super.onStop()
        Log.d("abc_mob", "onStop: ")
        // mobileViewModel.clear()
    }

    override fun onDestroy() {
// Stop the auto-scrolling when the activity is destroyed
        handler.removeCallbacks(runnable)
        super.onDestroy()
        Log.d("abc_mob", "onDestroy: ")
    }
    private fun openOptionsMenu(view: View) {

        val contextWrapper = ContextThemeWrapper(requireContext(), R.style.PopupMenuStyle)
        val popup = PopupMenu(contextWrapper, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.menu_top, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> showLogoutConfirmationDialog("Are you sure you want to logout?")

            }
            true
        }
        popup.show()
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
        finishAffinity(requireActivity())
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun setUpObserver() {
        try {
            reportViewModel.reportData.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            it.data?.let { qrData ->
                                if (qrData.status.code == "200") {
                                    if (qrData.data.isNotEmpty()) {
                                        binding.scanCount.text = qrData.data.size.toString()
                                    } else {
                                        Log.d("api_scan_res", "Data list is empty")
                                    }
                                }
                                else if (qrData.status.code == "401") {
                                    showLogoutConfirmationDialog(qrData.status.message_details.toString())
                                }
                                else if (qrData.status.code == "400") {
                                    showLogoutConfirmationDialog(qrData.status.message_details.toString())
                                }
                                else if (qrData.status.code == "204") {
                                    //TODO SAVE PREFERENCE TOAST
//                                    Const.shortToast(
//                                        requireContext(), "Welcome to GSFK 2024"
//                                    )
                                }
                                else {
                                    Log.d("abc_home", "json_$qrData")
                                    Const.shortToast(
                                        requireContext(), qrData.status.code.toString()
                                    )
                                }
                            }
                        }
                        Status.LOADING -> {
                        }
                        Status.ERROR -> {
                            Const.shortToast(requireContext(), it.message.toString())
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("abc_mobile", "setUpObserver: ", e)
        }
    }

}
