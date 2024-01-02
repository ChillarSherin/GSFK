package com.chillarcards.gsfk.data.api

import com.chillarcards.gsfk.data.model.*
import retrofit2.Response

/**
 * @Author: Sherin Jaison
 * @Date: 01-11-2023
 * Chillar
 */
class ApiHelperImpl(private val apiService: ApiService) : ApiHelper {
    override suspend fun getMobile(
        mobileNumber: String,
        password: String
    ): Response<MobileModel> {
        return apiService.getMobile(mobileNumber,  password)
    }
    override suspend fun getQrScan(
        mobileNumber: String,
        token: String,
        qrCode: String
    ): Response<ScanModel> {
        return apiService.getQrScan(mobileNumber,  token,  qrCode)
    }
    override suspend fun getEventScan(
        mobileNumber: String,
        token: String,
        eventId: List<String>
    ): Response<UpdateModel> {
        return apiService.getEventScan(mobileNumber,  token,  eventId)
    }
    override suspend fun getReport(
        mobileNumber: String,
        token: String,
        date: String
    ): Response<ReportModel> {
        return apiService.getReport(mobileNumber,  token,  date)
    }
}