package com.stop.model.route

data class Station(
    val index: Int,
    val coordinate: Coordinate,
    val stationID: String,
    val stationName: String
)