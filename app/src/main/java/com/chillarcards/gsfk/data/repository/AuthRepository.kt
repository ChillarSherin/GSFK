package com.chillarcards.gsfk.data.repository

import com.chillarcards.gsfk.data.api.ApiHelper

/**
 * @Author: Sherin Jaison
 * @Date: 01-11-2023
 * Chillar
 */
class AuthRepository(private val apiHelper: ApiHelper) {
    suspend fun getMobile(mobileNumber: String, password: String) =
        apiHelper.getMobile(mobileNumber,password)
    suspend fun getQrScan(mobileNumber: String, token: String, qrScan: String) =
        apiHelper.getQrScan(mobileNumber,token,qrScan)
    suspend fun getEventScan(mobileNumber: String, token: String, eventId: List<String>) =
        apiHelper.getEventScan(mobileNumber,token,eventId)
    suspend fun getReport(mobileNumber: String, token: String, date: String) =
        apiHelper.getReport(mobileNumber,token,date)

}