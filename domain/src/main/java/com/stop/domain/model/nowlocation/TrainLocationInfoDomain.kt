package com.stop.domain.model.nowlocation

import com.stop.domain.model.route.seoul.subway.TransportDirectionType

data class TrainLocationInfoDomain(
    val subwayId: String,
    val subwayName: String,
    val stationId: String,
    val currentStationName: String,
    val trainNumber: String,
    val trainStatus: String,
    val destinationStationName: String,
    val subwayDirection: TransportDirectionType,
    val isLastTrain: Boolean,
)
