package com.stop.data.remote.model.nowLocation

import com.squareup.moshi.Json

data class BusInfo(
    @Json(name = "stopFlag")
    val isArrivedAtStation: String,
    val sectionId: String,
    @Json(name = "gpsX")
    val latitude: String,
    @Json(name = "gpsY")
    val longitude: String,
    @Json(name = "vehId")
    val busId: String,
    @Json(name = "plainNo")
    val busNumber: String,
    @Json(name = "isrunyn")
    val isRun: String,
)
