package com.stop.domain.usecase.nearplace

import com.stop.domain.model.nearplace.Place

interface GetNearPlacesUseCase {

    suspend fun getNearPlaces(
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double
    ): List<Place>

}