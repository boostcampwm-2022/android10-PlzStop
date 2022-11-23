package com.stop.domain.model.route.tmap.origin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Station(
    val index: Int,
    val lat: String,
    val lon: String,
    val stationID: String,
    val stationName: String
)