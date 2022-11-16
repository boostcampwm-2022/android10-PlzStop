package com.stop.data.remote.model.route

import com.squareup.moshi.Json

data class RequestParameters(
    @Json(name = "airportCount")
    val airportCount: Int,
    @Json(name = "busCount")
    val busCount: Int,
    @Json(name = "endX")
    val endX: String,
    @Json(name = "endY")
    val endY: String,
    @Json(name = "expressbusCount")
    val expressbusCount: Int,
    @Json(name = "ferryCount")
    val ferryCount: Int,
    @Json(name = "locale")
    val locale: String,
    @Json(name = "maxWalkDistance")
    val maxWalkDistance: String,
    @Json(name = "reqDttm")
    val reqDttm: String,
    @Json(name = "startX")
    val startX: String,
    @Json(name = "startY")
    val startY: String,
    @Json(name = "subwayBusCount")
    val subwayBusCount: Int,
    @Json(name = "subwayCount")
    val subwayCount: Int,
    @Json(name = "trainCount")
    val trainCount: Int,
    @Json(name = "wideareaRouteCount")
    val wideareaRouteCount: Int
)