package com.stop.domain.model.nearplace

data class Place(
    val name: String,
    val radius : String,
    val roadAddressList: List<RoadAddress>
)
