package com.chillarcards.gsfk.ui.report

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.chillarcards.gsfk.R
import com.chillarcards.gsfk.databinding.FragmentReportBinding
import com.chillarcards.gsfk.ui.Dummy
import com.chillarcards.gsfk.ui.adapter.ReportAdapter
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Calendar

class ReportFragment : Fragment() {

    lateinit var binding: FragmentReportBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbar()


        val transItem = listOf(
            Dummy("Class A", 5, "Muhammad Hussain"),
            Dummy("Class B", 4, "Vihaan Mehta "),
            Dummy("Sky View", 5, "Daniel Jones "),
            Dummy("School", 20, "Reyansh K"),
            Dummy("Class B", 3, "Aditya Joshi "),

        )
        val reportAdapter = ReportAdapter(
            transItem, context)

        binding.tranRv.adapter = reportAdapter
        binding.tranRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)


        binding.printReport.setOnClickListener {
            try {
//                findNavController().navigate(
//                    HomeFragmentDirections.actionHomeFragmentToBookingFragment(
//                    )
//                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

     }

    private fun setToolbar() {
        binding.toolbar.toolbarBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.toolbarTitle.text = getString(R.string.sales_report)
    }

    //PRINT


}