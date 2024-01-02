package com.chillarcards.gsfk.ui.report

import android.content.Context
import android.content.Intent
import android.os.Build
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.chillarcards.gsfk.MainActivity
import com.chillarcards.gsfk.R
import com.chillarcards.gsfk.databinding.FragmentReportBinding
import com.chillarcards.gsfk.ui.adapter.ReportAdapter
import com.chillarcards.gsfk.utills.Const
import com.chillarcards.gsfk.utills.PrefManager
import com.chillarcards.gsfk.utills.Status
import com.chillarcards.gsfk.viewmodel.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReportFragment : Fragment() {

    lateinit var binding: FragmentReportBinding
    private val reportViewModel by viewModel<ReportViewModel>()
    private lateinit var prefManager: PrefManager

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
        prefManager = PrefManager(requireContext())

        binding.statusAnim.setAnimation(R.raw.no_data)
        binding.date.text = getCurrentDateStyle()

        reportViewModel.mobileNumber.value = prefManager.getMobileNo()
        reportViewModel.token.value = prefManager.getToken()
        reportViewModel.date.value = Const.getCurrentDate()
        reportViewModel.getReportResponse()
        setUpObserver()

     }

    private fun setToolbar() {
        binding.toolbar.toolbarBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.toolbarTitle.text = getString(R.string.sales_report)
    }

    private fun getCurrentDateStyle(): String {
        val currentDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
        return currentDate.format(formatter)
    }


    //API
    private fun setUpObserver() {
        try {
            reportViewModel.reportData.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            hideProgress()
                            it.data?.let { qrData ->
                                if (qrData.status.code == "200") {
                                    if (qrData.data.isNotEmpty()) {
                                        binding.custCount.text = qrData.data.size.toString()

                                        val reportAdapter = ReportAdapter(
                                            qrData.data, context)

                                        binding.tranRv.adapter = reportAdapter
                                        binding.tranRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                                    }
                                    else {
                                        Log.d("api_scan_res", "Data list is empty")
                                    }

                                }
                                else if (qrData.status.code == "401") {
                                    logout(requireContext(),qrData.status.message_details.toString())

                                }
                                else if(qrData.status.code == "204") {
                                    Log.d("abc_home", "json_$qrData")
                                    binding.tranRv.visibility = View.GONE
                                    binding.statusAnim.visibility = View.VISIBLE
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
    private fun logout(context: Context, value: String) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(value)
        builder.setNegativeButton(context.getString(R.string.logout)) { dialogInterface, _ ->
            val prefManager = PrefManager(requireContext())
            prefManager.clearAll()
            Const.clearCache(requireContext())
            prefManager.setIsLoggedIn(false)

            Log.d("abc_home", "showLogoutAlert: recreating activity.. all data cleared")
            val intent = Intent(requireContext(), MainActivity::class.java)
            ActivityCompat.finishAffinity(requireActivity())
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setOnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.resources.getColor(R.color.primary_red)
            )
        }
        alertDialog.setCanceledOnTouchOutside(false)
        alertDialog.show()
    }

}