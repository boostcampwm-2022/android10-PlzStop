package com.stop.domain.model.route.seoul.bus

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BusLineInfo(
    @Json(name = "busRouteNm")
    val busLineName: String,
    @Json(name = "busRouteId")
    val lineId: String,
    @Json(name = "term")
    val term: Int,
)
