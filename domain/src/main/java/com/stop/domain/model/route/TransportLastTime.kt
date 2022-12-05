package com.stop.domain.model.route

import com.stop.domain.model.route.seoul.subway.TransportDirectionType

data class TransportLastTime(
    val transportMoveType : TransportMoveType,
    val area : Area,
    val lastTime: String,
    val destinationStationName: String,
    val stationsUntilStart: List<TransportStation>,
    val enableDestinationStation: List<TransportStation>,
    val transportDirectionType: TransportDirectionType,
    val routeId: String,
)