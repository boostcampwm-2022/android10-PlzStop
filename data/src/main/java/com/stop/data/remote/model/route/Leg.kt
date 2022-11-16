package com.stop.data.remote.model.route

import com.squareup.moshi.Json

data class Leg(
    @Json(name = "distance")
    val distance: Double,
    @Json(name = "end")
    val end: End,
    @Json(name = "mode")
    val mode: String,
    @Json(name = "passShape")
    val passShape: PassShape?,
    @Json(name = "passStopList")
    val passStopList: PassStopList?,
    @Json(name = "route")
    val route: String?,
    @Json(name = "routeColor")
    val routeColor: String?,
    @Json(name = "sectionTime")
    val sectionTime: Double?,
    @Json(name = "start")
    val start: Start?,
    @Json(name = "steps")
    val steps: List<Step>?,
    @Json(name = "type")
    val type: Int?
)