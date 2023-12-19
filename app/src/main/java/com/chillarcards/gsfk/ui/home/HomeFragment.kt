package com.chillarcards.gsfk.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.chillarcards.gsfk.R
import com.chillarcards.gsfk.databinding.FragmentHomeBinding
import com.chillarcards.gsfk.ui.Org
import com.chillarcards.gsfk.ui.adapter.HorizontalAdapter
import com.chillarcards.gsfk.ui.adapter.ImageSliderAdapter

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
//                 HomeFragmentDirections.actionScanFragmentToResultFragment()
                 HomeFragmentDirections.actionHomeFragmentToScanFragment()
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

                else -> false
            }
        }

        popup.show()
    }


}
