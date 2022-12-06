package com.stop.domain.model.nowlocation

import com.squareup.moshi.Json

data class BusCurrentInformation(
    @Json(name = "plainNo")
    val licensePlateNumber: String,
    @Json(name = "vehId")
    val vehicleId: String,
    @Json(name = "sectOrd")
    val currentStationOrder: String,
    @Json(name = "stopFlag")
    val isStoppedAtStation: String,
    @Json(name = "gpsX")
    val longitude: String,
    @Json(name = "gpsY")
    val latitude: String,
    @Json(name = "isrunyn")
    val isRun: String,
    @Json(name = "islastyn")
    val isLast: String,
    @Json(name = "lastStnId")
    val beforeNodeId: String,
) {
    fun toUseCaseModel(transportState: TransportState) = BusCurrentInformationUseCaseItem(
        licensePlateNumber = licensePlateNumber,
        vehicleId = vehicleId,
        currentStationOrder = currentStationOrder,
        isStoppedAtStation = isStoppedAtStation,
        longitude = longitude,
        latitude = latitude,
        isRun = isRun,
        isLast = isLast,
        beforeNodeId = beforeNodeId,
        transportState = transportState,
    )
}

