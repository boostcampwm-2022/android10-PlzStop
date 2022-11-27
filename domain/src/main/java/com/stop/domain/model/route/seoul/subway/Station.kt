package com.stop.domain.model.route.seoul.subway

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Station(
    @Json(name = "STATION_CD")
    val stationCd: String,
    @Json(name = "STATION_NM")
    val stationName: String,
    @Json(name = "FR_CODE")
    val frCode: String,
)
