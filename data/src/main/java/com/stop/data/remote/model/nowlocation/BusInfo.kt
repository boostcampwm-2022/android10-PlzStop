package com.stop.data.remote.model.nowlocation

import com.squareup.moshi.Json
import com.stop.data.model.nowlocation.BusInfoRepositoryItem

data class BusInfo(
    @Json(name = "stopFlag")
    val isArrivedAtStation: String,
    val sectionId: String,
    @Json(name = "gpsX")
    val longitude: String,
    @Json(name = "gpsY")
    val latitude: String,
    @Json(name = "vehId")
    val busId: String,
    @Json(name = "plainNo")
    val busNumber: String,
    @Json(name = "isrunyn")
    val isRun: String,
) {
    fun toRepositoryModel() = BusInfoRepositoryItem(
        isArrivedAtStation,
        sectionId,
        latitude,
        longitude,
        busId,
        busNumber,
        isRun
    )
}
