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
import com.chillarcards.gsfk.utills.NetworkHelper
import com.chillarcards.gsfk.utills.Resource

/**
 * Created by Sherin on 27-12-2023.
 */

class MobileViewModel(
    private val authRepository: AuthRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    private val _mobileData = MutableLiveData<Resource<MobileModel>?>()
    val mobileData: LiveData<Resource<MobileModel>?> = _mobileData

    var mobileNumber = MutableLiveData<String>()
    var password = MutableLiveData<String>()


    fun getMobileResponse() {
        viewModelScope.launch(NonCancellable) {
            try {
                _mobileData.postValue(Resource.loading(null))
                if (networkHelper.isNetworkConnected()) {
                    authRepository.getMobile(mobileNumber.value.toString(), password.value.toString()).let {
                        if (it.isSuccessful) {
                            _mobileData.postValue(Resource.success(it.body()))
                        } else {
                            _mobileData.postValue(Resource.error(it.errorBody().toString(), null))
                        }
                    }
                } else {
                    _mobileData.postValue(Resource.error("No Internet Connection", null))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _mobileData.postValue(Resource.error(e.toString(), null))
            }
        }
    }

    fun clear() {
        _mobileData.value = null
    }
}