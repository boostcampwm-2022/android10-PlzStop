package com.stop.data.remote.model.nowlocation.subway

import com.squareup.moshi.JsonClass
import com.stop.data.model.nowlocation.SubwayTrainRealTimePositionRepositoryItem

@JsonClass(generateAdapter = true)
data class SubwayTrainNowLocationResponse(
    val realtimePositionList: List<TrainLocationInfo>
) {
    fun toRepositoryModel(trainNumber: String): SubwayTrainRealTimePositionRepositoryItem {
        return realtimePositionList.first { it.trainNumber == trainNumber }.run {
             SubwayTrainRealTimePositionRepositoryItem(
                subwayId = subwayId,
                subwayName = stationName,
                stationId = stationId,
                stationName = stationName,
                trainNumber = this.trainNumber,
                trainStatus = trainStatus
            )
        }
    }



}
