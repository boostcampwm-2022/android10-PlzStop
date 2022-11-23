package com.stop.domain.model.route.tmap.origin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Leg(
    val distance: Double,
    val end: End,
    val mode: String,
    val passShape: PassShape?,
    val passStopList: PassStopList?,
    val route: String?,
    val routeColor: String?,
    val sectionTime: Double,
    val start: Start,
    val steps: List<Step>?,
    val type: Int?
)