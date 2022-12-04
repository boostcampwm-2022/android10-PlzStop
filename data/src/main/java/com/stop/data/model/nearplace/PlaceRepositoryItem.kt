package com.stop.data.model.nearplace

import com.stop.data.local.model.RecentPlaceSearchEntity
import com.stop.domain.model.nearplace.PlaceUseCaseItem
import com.stop.domain.model.nowlocation.NowStationLocationUseCaseItem

data class PlaceRepositoryItem(
    val name: String,
    val radius: String,
    val fullAddressRoad: String,
    val centerLat: Double,
    val centerLon: Double
) {

    fun toUseCaseModel() = PlaceUseCaseItem(
        name = name,
        radius = radius,
        fullAddressRoad = fullAddressRoad,
        centerLat = centerLat,
        centerLon = centerLon
    )

    fun toDataSourceModel() = RecentPlaceSearchEntity(
        name = name,
        radius = radius,
        fullAddressRoad = fullAddressRoad,
        centerLat = centerLat,
        centerLon = centerLon,
    )

    fun toNowStationLocationUseCaseModel() = NowStationLocationUseCaseItem(
        name = name,
        latitude = centerLat.toString(),
        longitude = centerLon.toString()
    )
}
