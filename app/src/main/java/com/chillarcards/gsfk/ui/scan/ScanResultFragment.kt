package com.chillarcards.gsfk.ui.scan

import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.chillarcards.gsfk.R
import com.chillarcards.gsfk.databinding.FragmentScanResultBinding
import com.chillarcards.gsfk.interfaces.IAdapterViewUtills
import com.chillarcards.gsfk.ui.Dummy
import com.chillarcards.gsfk.ui.adapter.ResultAdapter
import com.chillarcards.gsfk.utills.CommonDBaseModel
import com.chillarcards.gsfk.utills.Const


class ScanResultFragment : Fragment(), IAdapterViewUtills {

    lateinit var binding: FragmentScanResultBinding
    private var mediaPlayer: MediaPlayer? = null

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

        Const.enableButton(binding.backBtn)

        val transItem = listOf(
            Dummy("4", 1, "Event Entry"),
            Dummy("1", 0, "Brain and Cognition"),
            Dummy("2", 0, "VR Experience"),
            Dummy("1", 0, "War"),
            Dummy("4", 0, "Immersive Experience"),

        )
        val transactionAdapter = ResultAdapter(
            transItem, context,this@ScanResultFragment)

        binding.tranRv.adapter = transactionAdapter
        binding.tranRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.help.paintFlags = binding.help.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.backBtn.setOnClickListener{
            findNavController().popBackStack()
        }

        binding.checkinBtn.setOnClickListener{
            // Initialize MediaPlayer in onCreate or another appropriate method
            mediaPlayer = MediaPlayer.create(context, R.raw.bell_audio)
            if (!mediaPlayer!!.isPlaying) {
                mediaPlayer!!.start()
            }

            //TODO API CALL to update entry
            findNavController().popBackStack()
        }
    }

    private fun setToolbar() {
        binding.toolbar.toolbarBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.toolbarTitle.text = getString(R.string.app_full)
    }

    override fun getAdapterPosition(
        Position: Int,
        ValueArray: ArrayList<CommonDBaseModel>,
        Mode: String?
    ) {
        if(Mode.equals("CheckIn")) {
            Const.enableButton(binding.checkinBtn)

            Toast.makeText(requireContext(),ValueArray[0].valueStr1.toString(),Toast.LENGTH_SHORT).show()

        }
    }
}