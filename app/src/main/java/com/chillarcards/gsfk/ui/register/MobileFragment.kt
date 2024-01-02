package com.chillarcards.gsfk.ui.register

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chillarcards.gsfk.R
import com.chillarcards.gsfk.databinding.FragmentMobileBinding
import com.chillarcards.gsfk.utills.Const
import com.chillarcards.gsfk.utills.PrefManager
import com.chillarcards.gsfk.utills.Status
import com.chillarcards.gsfk.viewmodel.MobileViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MobileFragment : Fragment() {

    lateinit var binding: FragmentMobileBinding

    private val mobileRegex = "^[7869]\\d{9}$".toRegex()
    private val emailRegex = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})".toRegex()

    private var statusTrue: Boolean = false
    private val mobileViewModel by viewModel<MobileViewModel>()
    private lateinit var prefManager: PrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMobileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val pInfo =
            activity?.let { activity?.packageManager!!.getPackageInfo(it.packageName, PackageManager.GET_ACTIVITIES) }
        val versionName = pInfo?.versionName //Version Name
        prefManager = PrefManager(requireContext())

        binding.version.text = "${getString(R.string.version)}" + Const.ver_title + versionName

        binding.mobileEt.addTextChangedListener {
            val input = it.toString()
            if (input.isNotEmpty()) {
                if (!input.matches(mobileRegex) ) {
                    binding.mobile.error = "Enter a valid mobile number"
                    Const.disableButton(binding.loginBtn)
                }
                else {
                    binding.mobile.error = null
                    binding.mobile.isErrorEnabled = false
                    Const.enableButton(binding.loginBtn)
                    val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
            } else {
                binding.mobile.error = null
                binding.mobile.isErrorEnabled = false
            }
        }

        binding.passwordEt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }
            false
        }


        binding.loginBtn.setOnClickListener {
            val mobileInput = binding.mobileEt.text.toString()
            val passwordInput = binding.passwordEt.text.toString()

            if (!mobileRegex.containsMatchIn(mobileInput)) {
                binding.mobile.error = getString(R.string.mob_validation)
            } else if (passwordInput.isEmpty()) {
                binding.passwordEt.error = getString(R.string.password_empty_error)
            } else {
                binding.mobile.error = null
                binding.passwordEt.error = null

                mobileViewModel.mobileNumber.value = mobileInput
                mobileViewModel.password.value = passwordInput
                mobileViewModel.getMobileResponse()
            }
        }

        setUpObserver()
        setTextColorForTerms()
    }


    private fun setUpObserver() {
        try {
            mobileViewModel.mobileData.observe(viewLifecycleOwner) {
                if (it != null) {
                    when (it.status) {
                        Status.SUCCESS -> {
                            hideProgress()
                            it.data?.let { mobileData ->
                                when (mobileData.status.code) {
                                    "200" -> {
                                        prefManager.setToken(mobileData.data.userToken ?: "")
                                        prefManager.setMobileNo(mobileData.data.userPhone)
                                        prefManager.setIsLoggedIn(true)

                                        findNavController().navigate(
                                            MobileFragmentDirections.actionMobileFragmentToHomeFragment()
                                        )

                                        // TODO: Check effects of the following code
                                        mobileViewModel.mobileData.removeObservers(viewLifecycleOwner)
                                    }
                                    "500" -> {
                                        Const.shortToast(
                                            requireContext(),
                                            mobileData.status.message.toString()
                                        )
                                    }
                                    else -> {
                                        mobileData.status.message?.let { it1 ->
                                            Const.shortToast(
                                                requireContext(),
                                                it1
                                            )
                                        }
                                    }
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
        binding.loginBtn.visibility = View.INVISIBLE
    }

    private fun hideProgress() {
        binding.loginProgress.visibility = View.GONE
        binding.loginBtn.visibility = View.VISIBLE
    }

    private fun setTextColorForTerms() {
        try {
            val s = "Terms and Conditions"
            val wordToSpan: Spannable = SpannableString(s)
            val click: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val browserIntent =
                        Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://www.gsfk.org/privacy-policy/")
                        )
                    startActivity(browserIntent)

                }
            }
            wordToSpan.setSpan(
                click, s.indexOf("Terms"), s.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            wordToSpan.setSpan(
                StyleSpan(Typeface.BOLD), s.indexOf("Terms"), s.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            wordToSpan.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.white
                    )
                ),
                s.indexOf("Terms"), s.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.terms1.text = wordToSpan
            binding.terms1.movementMethod = LinkMovementMethod.getInstance()
        } catch (e: Exception) {
            Log.e("abc_mobile", "setTextColorForTerms: msg: ", e)
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d("abc_mob", "onStop: ")
        binding.passwordEt.setText("")
        binding.mobileEt.setText("")

        mobileViewModel.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("abc_mob", "onDestroy: ")
    }
}