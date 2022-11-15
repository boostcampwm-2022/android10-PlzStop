package com.stop.remote.model.nearplace

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NewAddres(
    val bldNo1: String,
    val bldNo2: String,
    val centerLat: String,
    val centerLon: String,
    val frontLat: String,
    val frontLon: String,
    val fullAddressRoad: String,
    val roadId: String,
    val roadName: String
)