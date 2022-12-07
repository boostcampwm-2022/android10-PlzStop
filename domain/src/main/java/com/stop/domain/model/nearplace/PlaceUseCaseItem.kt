package com.stop.domain.model.nearplace

data class PlaceUseCaseItem(
    val name: String,
    val radius: String,
    val fullAddressRoad: String,
    val centerLat: Double,
    val centerLon: Double
)
