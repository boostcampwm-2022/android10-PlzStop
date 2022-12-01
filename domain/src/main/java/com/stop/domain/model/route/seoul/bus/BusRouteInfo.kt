package com.stop.domain.model.route.seoul.bus

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BusRouteInfo(
    @Json(name = "busRouteNm")
    val busRouteName: String,
    @Json(name = "busRouteId")
    val routeId: String,
    @Json(name = "term")
    val term: Int,
)
