package com.stop.data.model.nearplace

import com.stop.domain.model.nearplace.Place

data class Place(
    val name: String,
    val roadAddressList: List<RoadAddress>
) {

    fun toUseCaseModel() = Place(
        name = name,
        roadAddressList = roadAddressList.map {
           it.toDomainRoadAddress()
        }
    )
}
