package com.stop.data.remote.model.nowlocation.subway

import android.util.Log
import com.squareup.moshi.JsonClass
import com.stop.data.model.nowlocation.SubwayTrainRealTimePositionRepositoryItem

@JsonClass(generateAdapter = true)
data class SubwayTrainNowLocationResponse(
    val realtimePositionList: List<TrainLocationInfo>
) {
    fun toRepositoryModel(trainNumber: String): SubwayTrainRealTimePositionRepositoryItem {
        Log.d("MissionFragment","mission ${realtimePositionList.first { it.trainNumber == trainNumber }}")

        return realtimePositionList.first { it.trainNumber == trainNumber }.run {
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
