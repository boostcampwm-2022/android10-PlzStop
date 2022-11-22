package com.stop.domain.model.route.tmap.custom

data class Station(
    val orderIndex: Int,
    val coordinate: Coordinate,
    val stationId: String,
    val stationName: String,
    val idUsedAtPublicApi: String,
)
