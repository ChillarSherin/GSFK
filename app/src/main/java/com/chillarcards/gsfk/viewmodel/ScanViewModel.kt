package com.chillarcards.gsfk.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chillarcards.gsfk.data.model.ScanModel
import com.chillarcards.gsfk.data.model.UpdateModel
import com.chillarcards.gsfk.data.repository.AuthRepository
import com.chillarcards.gsfk.utills.NetworkHelper
import com.chillarcards.gsfk.utills.Resource
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch

/**
 * Created by Sherin on 28-12-2023.
 */

class ScanViewModel(
    private val authRepository: AuthRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {
    private val _scanData = MutableLiveData<Resource<ScanModel>>()
    val scanData: LiveData<Resource<ScanModel>> get() = _scanData
    private val _scanUpdateData = MutableLiveData<Resource<UpdateModel>>()
    val scanUpdateData: LiveData<Resource<UpdateModel>> get() = _scanUpdateData

    var idList: MutableList<String> = mutableListOf()
    var mobileNumber = MutableLiveData<String>()
    var token = MutableLiveData<String>()
    var qrScan = MutableLiveData<String>()
    var itemSelId: MutableLiveData<String> = MutableLiveData()
    var idListSele: MutableList<String> = mutableListOf()

    private fun addNewId(newId: String) {
        idList.add(newId)
        if (idList.size > 1) {
            val previousId = idList[0]
            idList.remove(previousId)
        }
    }

    private fun getCurrentIds(): List<String> {
        addNewId(itemSelId.value.toString())

        return idList.toList()
    }

    fun getScanResponse() {
        viewModelScope.launch(NonCancellable) {
            try {
                _scanData.postValue(Resource.loading(null))
                if (networkHelper.isNetworkConnected()) {
                    Log.d("api_scan", "MobileNumber: ${mobileNumber.value}, Token: ${token.value}, QRScan: ${qrScan.value}")

                    authRepository.getQrScan(mobileNumber.value.toString(),
                        token.value.toString(),
                        qrScan.value.toString()).let {
                        if (it.isSuccessful) {
                            _scanData.postValue(Resource.success(it.body()))
                        } else {
                            _scanData.postValue(Resource.error(it.errorBody().toString(), null))
                        }
                    }
                } else {
                    _scanData.postValue(Resource.error("No Internet Connection", null))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("abc_smodel", "onCatch: "+e.printStackTrace())
                _scanData.postValue(Resource.error(e.toString(), null))
            }
        }
    }

    fun updateScanResponse() {
        viewModelScope.launch(NonCancellable) {
            try {
                _scanUpdateData.postValue(Resource.loading(null))
                if (networkHelper.isNetworkConnected()) {


                    Log.d("api_scan", "MobileNumber: ${mobileNumber.value}, Token: ${token.value}, dataId:"+ getCurrentIds())
                    Log.d("api_scan", idListSele.toString())

                    authRepository.getEventScan(mobileNumber.value.toString(),
                        token.value.toString(),
                        listOf(idListSele.toString())
                    ).let {
                        if (it.isSuccessful) {
                            _scanUpdateData.postValue(Resource.success(it.body()))
                        } else {
                            _scanUpdateData.postValue(Resource.error(it.errorBody().toString(), null))
                        }
                    }
                } else {
                    _scanUpdateData.postValue(Resource.error("No Internet Connection", null))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("abc_smodel", "onCatch: "+e.printStackTrace())
                _scanUpdateData.postValue(Resource.error(e.toString(), null))
            }
        }
    }

    fun clear() {
//        _scanData.value = ""
    }
}