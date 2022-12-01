package com.stop.data.remote.model.nowlocation.bus

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
        isArrivedAtStation = isArrivedAtStation,
        sectionId = sectionId,
        latitude = latitude,
        longitude = longitude,
        busId = busId,
        busNumber = busNumber,
        isRun = isRun,
    )
}