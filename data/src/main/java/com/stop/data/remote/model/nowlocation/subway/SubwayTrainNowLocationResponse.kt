package com.stop.data.remote.model.nowlocation.subway

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.stop.data.model.nowlocation.SubwayTrainRealTimePositionRepositoryItem

@JsonClass(generateAdapter = true)
data class SubwayTrainNowLocationResponse(
    @Json(name = "realtimePositionList")
    val realtimePositions: List<TrainLocationInfo>
) {
    fun toRepositoryModel(trainNumber: String): SubwayTrainRealTimePositionRepositoryItem {
        return realtimePositions.first { it.trainNumber == trainNumber }.run {
             SubwayTrainRealTimePositionRepositoryItem(
                subwayId = subwayId,
                subwayName = stationName,
                stationId = stationId,
                stationName = stationName + STATION,
                trainNumber = this.trainNumber,
                trainStatus = trainStatus
            )
        }
    }

    companion object {
        private const val STATION = "역"
    }
}
