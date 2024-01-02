package com.chillarcards.gsfk.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import com.chillarcards.gsfk.data.repository.AuthRepository
import com.chillarcards.gsfk.data.model.MobileModel
import com.chillarcards.gsfk.data.model.ReportModel
import com.chillarcards.gsfk.utills.NetworkHelper
import com.chillarcards.gsfk.utills.Resource

/**
 * Created by Sherin on 27-12-2023.
 */

class ReportViewModel(
    private val authRepository: AuthRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val _reportData = MutableLiveData<Resource<ReportModel>>()
    val reportData: LiveData<Resource<ReportModel>> get() = _reportData

    var mobileNumber = MutableLiveData<String>()
    var token = MutableLiveData<String>()
    var date = MutableLiveData<String>()


    fun getReportResponse() {
        viewModelScope.launch(NonCancellable) {
            try {
                _reportData.postValue(Resource.loading(null))
                if (networkHelper.isNetworkConnected()) {
                    Log.d("api_report", "MobileNumber: ${mobileNumber.value}, Token: ${token.value}, Date: ${date.value}")

                    authRepository.getReport(mobileNumber.value.toString(), token.value.toString(),date.value.toString()).let {
                        if (it.isSuccessful) {
                            _reportData.postValue(Resource.success(it.body()))
                        } else {
                            _reportData.postValue(Resource.error(it.errorBody().toString(), null))
                        }
                    }
                } else {
                    _reportData.postValue(Resource.error("No Internet Connection", null))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _reportData.postValue(Resource.error(e.toString(), null))
            }
        }
    }

    fun clear() {
//        _reportData.value = ""
    }
}