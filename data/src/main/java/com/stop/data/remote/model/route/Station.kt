package com.stop.data.remote.model.route

import com.squareup.moshi.Json

data class Station(
    @Json(name = "index")
    val index: Int,
    @Json(name = "lat")
    val lat: String,
    @Json(name = "lon")
    val lon: String,
    @Json(name = "stationID")
    val stationID: String,
    @Json(name = "stationName")
    val stationName: String
)