package com.stop.data.model.nearplace

import com.stop.domain.model.nearplace.Place

data class Place(
    val name: String,
    val radius: String,
    val fullAddressRoad: String,
    val centerLat: Double,
    val centerLon: Double
) {

    fun toUseCaseModel() = Place(
        name = name,
        radius = radius,
        fullAddressRoad = fullAddressRoad,
        centerLat = centerLat,
        centerLon = centerLon
    )
}
