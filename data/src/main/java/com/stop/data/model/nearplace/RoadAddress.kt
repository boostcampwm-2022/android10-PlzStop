package com.stop.data.model.nearplace

import com.stop.domain.model.nearplace.RoadAddress

data class RoadAddress(
    val bldNo1: String,
    val bldNo2: String,
    val centerLat: Float,
    val centerLon: Float,
    val frontLat: Float,
    val frontLon: Float,
    val fullAddressRoad: String,
    val roadId: String,
    val roadName: String
) {

    fun toDomainRoadAddress() = RoadAddress(
        bldNo1,
        bldNo2,
        centerLat,
        centerLon,
        frontLat,
        frontLon,
        fullAddressRoad,
        roadId,
        roadName
    )

}
