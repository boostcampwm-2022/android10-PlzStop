package com.stop.data.model.alarm

import com.stop.data.local.model.RecentPlaceSearchEntity
import com.stop.domain.model.nearplace.RecentPlaceSearch

data class RecentPlaceSearchItem(
    val name: String,
    val radius: String,
    val fullAddressRoad: String,
    val centerLat: Double,
    val centerLon: Double
) {

    fun toUseCaseModel() = RecentPlaceSearch(
        name = name,
        radius = radius,
        fullAddressRoad = fullAddressRoad,
        centerLat = centerLat,
        centerLon = centerLon,
    )

    fun toDataSourceModel() = RecentPlaceSearchEntity(
        name = name,
        radius = radius,
        fullAddressRoad = fullAddressRoad,
        centerLat = centerLat,
        centerLon = centerLon,
    )

}