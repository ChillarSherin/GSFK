package com.chillarcards.gsfk.data.api

import com.chillarcards.gsfk.data.model.*
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * @Author: Sherin Jaison
 * @Date: 02-11-2023$
 * Chillar
 */

interface ApiService {

    @FormUrlEncoded
    @POST("r_login")
    suspend fun getMobile(
        @Field("userPhone") mobile: String,
        @Field("userPassword") password: String
    ): Response<MobileModel>

    @FormUrlEncoded
    @POST("r_qr")
    suspend fun getQrScan(
        @Field("userPhone") mobile: String,
        @Field("userToken") token: String,
        @Field("qr") qrCode: String
    ): Response<ScanModel>

    @FormUrlEncoded
    @POST("u_item")
    suspend fun getEventScan(
        @Field("userPhone") mobile: String,
        @Field("userToken") token: String,
        @Field("event_transaction_item_id") eventId: List<String>
    ): Response<UpdateModel>
   @FormUrlEncoded
    @POST("r_report")
    suspend fun getReport(
        @Field("userPhone") mobile: String,
        @Field("userToken") token: String,
        @Field("date") date: String
    ): Response<ReportModel>

}