package com.stop.data.model.nearplace

import com.stop.domain.model.nearplace.Place

data class Place(
    val name: String,
    val radius : String,
    val roadAddressList: List<RoadAddress>
) {

    fun toUseCaseModel() = Place(
        name = name,
        radius = radius,
        roadAddressList = roadAddressList.map {
           it.toDomainRoadAddress()
        }
    )
}
