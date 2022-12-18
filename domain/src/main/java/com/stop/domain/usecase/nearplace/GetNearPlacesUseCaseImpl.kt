package com.stop.domain.usecase.nearplace

import com.stop.domain.model.nearplace.PlaceUseCaseItem
import com.stop.domain.repository.NearPlaceRepository
import javax.inject.Inject

internal class GetNearPlacesUseCaseImpl @Inject constructor(
    private val nearPlaceRepository: NearPlaceRepository
) : GetNearPlacesUseCase {

    override suspend operator fun invoke(
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double,
    ): List<PlaceUseCaseItem> =
        nearPlaceRepository.getNearPlaces(
            searchKeyword,
            centerLon,
            centerLat
        )

}
