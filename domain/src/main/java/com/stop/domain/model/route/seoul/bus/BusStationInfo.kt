package com.stop.domain.model.route.seoul.bus

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BusStationInfo(
    val arsId: String,
    @Json(name = "stNm")
    val stationName: String,
    @Json(name = "tmX")
    val longitude: String,
    @Json(name = "tmY")
    val latitude: String,
)
