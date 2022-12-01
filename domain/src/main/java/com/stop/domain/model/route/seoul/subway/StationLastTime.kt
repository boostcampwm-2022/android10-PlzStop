package com.stop.domain.model.route.seoul.subway

import com.squareup.moshi.Json

data class StationLastTime(
    @Json(name = "FR_CODE")
    val frCode: String,
    @Json(name = "INOUT_TAG")
    val inoutTag: String,
    @Json(name = "LEFTTIME")
    val leftTime: String,
    @Json(name = "STATION_CD")
    val stationCd: String,
    @Json(name = "STATION_NM")
    val stationName: String,
    @Json(name = "SUBWAYENAME")
    val destinationStationName: String,
    @Json(name = "WEEK_TAG")
    val weekType: String
)