package com.stop.data.remote.model.nowlocation.subway

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.stop.domain.model.ApiChangedException
import com.stop.domain.model.nowlocation.TrainLocationInfoDomain
import com.stop.domain.model.route.seoul.subway.TransportDirectionType

@JsonClass(generateAdapter = true)
data class TrainLocationInfo(
    val subwayId: String,
    @Json(name = "subwayNm")
    val subwayName: String,
    @Json(name = "statnId")
    val stationId: String,
    @Json(name = "statnNm")
    val currentStationName: String,
    @Json(name = "trainNo")
    val trainNumber: String,
    @Json(name = "trainSttus")
    val trainStatus: String, // 0:진입 1:도착, 0,1외 나머지는:출발
    @Json(name = "statnTnm")
    val destinationStationName: String,
    @Json(name = "updnLine")
    val subwayDirection: String,
    @Json(name = "lstcarAt")
    val isLastTrain: String, // (1:막차, 0:아님)
) {
    fun toDomain() = TrainLocationInfoDomain(
        subwayId = subwayId,
        subwayName = subwayName,
        stationId = stationId,
        currentStationName = currentStationName,
        trainNumber = trainNumber,
        trainStatus = trainStatus,
        destinationStationName = destinationStationName,
        subwayDirection = when (subwayDirection) {
            "1" -> TransportDirectionType.INNER
            "0" -> TransportDirectionType.OUTER
            else -> throw ApiChangedException()
        },
        isLastTrain = when (isLastTrain) {
            "1" -> true
            "0" -> false
            else -> throw ApiChangedException()
        },
    )
}
