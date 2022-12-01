package com.stop.data.model.nearplace

import com.stop.domain.model.nearplace.Place
import com.stop.domain.model.nowlocation.NowStationLocationUseCaseItem

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

    fun toNowStationLocationUseCaseModel() = NowStationLocationUseCaseItem(
        name = name,
        latitude = centerLat.toString(),
        longitude = centerLon.toString()
    )
}
