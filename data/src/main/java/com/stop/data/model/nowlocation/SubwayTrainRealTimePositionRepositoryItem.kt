package com.stop.data.model.nowlocation

import com.stop.domain.model.nowlocation.SubwayTrainRealTimePositionUseCaseItem

data class SubwayTrainRealTimePositionRepositoryItem(
    val subwayId: String,
    val subwayName: String,
    val stationId: String,
    val stationName: String,
    val trainNumber: String,
    val trainStatus: String
) {
    fun toUseCaseModel() = SubwayTrainRealTimePositionUseCaseItem(
        subwayId = subwayId,
        subwayName = subwayName,
        stationId = stationId,
        stationName = stationName,
        trainNumber = trainNumber,
        trainStatus = trainStatus
    )
}
