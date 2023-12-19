package com.chillarcards.gsfk.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.chillarcards.gsfk.MainActivity
import com.chillarcards.gsfk.R
import com.chillarcards.gsfk.databinding.FragmentHomeBaseBinding
import com.chillarcards.gsfk.utills.PrefManager
import com.google.android.material.bottomsheet.BottomSheetDialog

class HomeBaseFragment : Fragment() {

    lateinit var binding: FragmentHomeBaseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_base, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//
//        val navHostFragment = childFragmentManager
//            .findFragmentById(R.id.inner_host_nav) as NavHostFragment
//        val navController = navHostFragment.navController
//
////        binding.report.setOnClickListener {
////            navController.navigate(R.id.reportFragment)
////        }
////        binding.setting.setOnClickListener {
////            navController.navigate(R.id.generalFragment)
////        }
////        binding.addProfile.setOnClickListener {
////            navController.navigate(R.id.profileFragment)
////        }
//

//
//        binding.logout.setOnClickListener {
//            setBottomSheet()
//        }
//
//        navController.addOnDestinationChangedListener { controller, destination, arguments ->
//            when (destination.id) {
//                R.id.scanFragment  -> {
//                    binding.bottomMenu.visibility = View.GONE
//                }
//                else -> {
//                    binding.bottomMenu.visibility = View.VISIBLE
//                }
//            }
//        }


     }

    private fun setBottomSheet() {

        val bottomSheetView = LayoutInflater.from(context).inflate(R.layout.logout, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)

        val completeButton: TextView = bottomSheetView.findViewById(R.id.cancelButton)
        completeButton.setOnClickListener {

            bottomSheetDialog.dismiss()
        }

        val callButton: TextView = bottomSheetView.findViewById(R.id.okButton)
        callButton.setOnClickListener {
            performLogout()
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()

    }

    private fun performLogout() {

        // Clear any user session or authentication data
        clearUserSession()

        Log.d("abc_home", "showLogoutAlert: recreating activity.. all data cleared")
        val intent = Intent(requireContext(), MainActivity::class.java)
        ActivityCompat.finishAffinity(requireActivity())
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun clearUserSession() {

//        val preferences = getSharedPreferences("user_data", MODE_PRIVATE)
//        val editor = preferences.edit()
//        editor.clear()
//        editor.apply()

        val prefManager = PrefManager(requireContext())
        prefManager.clearAll()
    }

}