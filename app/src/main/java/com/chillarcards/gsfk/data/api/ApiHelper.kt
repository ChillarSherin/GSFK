package com.chillarcards.gsfk.data.api

import com.chillarcards.gsfk.data.model.*
import retrofit2.Response

/**
 * @Author: Sherin Jaison
 * @Date: 01-11-2023
 * Chillar
 */
interface ApiHelper {
    suspend fun getMobile(
        mobileNumber: String,
        password: String
    ): Response<MobileModel>

    suspend fun getQrScan(
        mobileNumber: String,
        token: String,
        qrCode: String
    ): Response<ScanModel>
    suspend fun getEventScan(
        mobileNumber: String,
        token: String,
        eventId: List<String>
    ): Response<UpdateModel>
    suspend fun getReport(
        mobileNumber: String,
        token: String,
        date: String
    ): Response<ReportModel>
}