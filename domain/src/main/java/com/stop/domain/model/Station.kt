package com.stop.domain.model

data class Station(
    val orderIndex: Int,
    val coordinate: Coordinate,
    val stationId: String,
    val stationName: String,
)
