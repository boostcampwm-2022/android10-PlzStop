package com.stop.remote.model.nearplace


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewAddressList(
    val newAddress: List<NewAddres>
)