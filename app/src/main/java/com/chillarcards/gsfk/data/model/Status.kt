package com.chillarcards.gsfk.data.model

import com.google.gson.annotations.SerializedName

data class Status (

    @SerializedName("code"            ) var code           : String? = null,
    @SerializedName("message"         ) var message        : String? = null,
    @SerializedName("message_details" ) var message_details : String? = null

)