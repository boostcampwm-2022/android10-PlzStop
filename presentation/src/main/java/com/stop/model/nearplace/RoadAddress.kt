package com.stop.model.nearplace

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
)