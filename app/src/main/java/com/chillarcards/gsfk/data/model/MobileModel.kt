package com.chillarcards.gsfk.data.model

data class MobileModel(
    val data: MDetails,
    val status: Status
)

data class MDetails(
    val userPhone: String,
    val userName: String,
    val userToken: String?
)
