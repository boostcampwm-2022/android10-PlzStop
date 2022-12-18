package com.stop.domain.usecase.nearplace

import com.stop.domain.model.nearplace.PlaceUseCaseItem

interface GetNearPlacesUseCase {

    suspend operator fun invoke(
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double
    ): List<PlaceUseCaseItem>

}