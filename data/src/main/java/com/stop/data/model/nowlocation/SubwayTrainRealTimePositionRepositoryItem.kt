package com.stop.data.model.nowlocation

data class SubwayTrainRealTimePositionRepositoryItem(
    val subwayId: String,
    val subwayName: String,
    val stationId: String,
    val stationName: String,
    val trainNumber: String,
    val trainStatus: String
)
