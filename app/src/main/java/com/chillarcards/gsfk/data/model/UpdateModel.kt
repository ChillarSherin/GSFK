package com.chillarcards.gsfk.data.model

data class UpdateModel(
    val data: Done,
    val status: Status
)

data class Done(
    val userPhone: String,
    val userName: String,
    val userToken: String?
)
