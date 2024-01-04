package com.chillarcards.gsfk.di.module


object ConfigBuild {

    // Field from build type: debug
    val DEBUG: Boolean = java.lang.Boolean.parseBoolean("true")

    //Need to change MainActivity  page for installation priority immediate or flexi
    // Field from build type: debug
//    const val BASE_URL = "http://dev.chillarcards.com/gsfk/device_api/v1_0/volunteer/android/"
    const val BASE_URL = "https://gsfk.chillarpayments.com/device_api/v1_0/volunteer/android/"

}
