package com.stop.domain.model.route.tmap.origin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestParameters(
    val airportCount: Int,
    val busCount: Int,
    val endX: String,
    val endY: String,
    val expressbusCount: Int,
    val ferryCount: Int,
    val locale: String,
    val maxWalkDistance: String,
    val reqDttm: String,
    val startX: String,
    val startY: String,
    val subwayBusCount: Int,
    val subwayCount: Int,
    val trainCount: Int,
    val wideareaRouteCount: Int
)