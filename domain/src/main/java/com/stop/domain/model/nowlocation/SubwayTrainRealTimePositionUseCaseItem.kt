package com.stop.domain.model.nowlocation

data class SubwayTrainRealTimePositionUseCaseItem(
    val subwayId: String,
    val subwayName: String,
    val stationId: String,
    val stationName: String,
    val trainNumber: String,
    val trainStatus: String
)
