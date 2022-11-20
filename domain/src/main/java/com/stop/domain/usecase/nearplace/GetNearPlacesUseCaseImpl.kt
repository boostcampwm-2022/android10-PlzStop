package com.stop.domain.usecase.nearplace

import com.stop.domain.model.nearplace.Place
import com.stop.domain.repository.NearPlaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class GetNearPlacesUseCaseImpl @Inject constructor(
    private val nearPlaceRepository: NearPlaceRepository
) : GetNearPlacesUseCase {

    override suspend fun getNearPlaces(
        version: Int,
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double,
        appKey: String
    ): Flow<List<Place>> =
        nearPlaceRepository.getNearPlaces(
            version,
            searchKeyword,
            centerLon,
            centerLat,
            appKey
        )

}
