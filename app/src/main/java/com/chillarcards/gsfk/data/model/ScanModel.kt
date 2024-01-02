package com.chillarcards.gsfk.data.model

import com.google.gson.annotations.SerializedName

data class ScanModel(
    @SerializedName("status" ) var status : Status?         = Status(),
   // @SerializedName("data") var data: List<RpData>
    @SerializedName("data") var data: List<RpData>
)


data class RpData (
    var event_transaction_id         : String,
    //  @SerializedName("event_transaction_id"            ) var eventTransactionId         : String,
    var event_transaction_uuid       : String,
    var userId                     : String,
    var event_transaction_amount     : String,
    var event_transaction_status     : String,
    var created_date_time            : String,
    var need_disability_support      : String,
    var person_json                 : String,
    var event_transaction_item_id     : String,
    var sub_package_id               : String,
    var event_transaction_item_status : String,
    var event_transaction_date       : String,
    var updated_date_time            : String,
    var approved_scan_count          : String,
    var scaned_count                : String,
    var sub_package_count            : String,
    var sub_package_price            : String,
    var itemTotalPrice             : String,
    var system_user_id               : String,
    var is_complimentary            : String,
    var package_id                  : String,
    var sub_package_uuid             : String,
    var sub_package_status           : String,
    var sub_package_name             : String,
    var max_allowed_Person           : String,
    var min_allowed_Person           : String,
    var age_group_id                 : String,
    var event_max_booking_days_allowed : String,
    var event_min_booking_days_allowed : String,
    var allowed_free_entry_count      : String,
    var allowed_free_entry_age_group_id : String,
    var full_name                   : String,
    var mobile_number               : String,
    var password                   : String,
    var user_status                 : String,
    var email                      : String,
    var district_name               : String,
    var gender                     : String,
    var age_category_id              : String

)