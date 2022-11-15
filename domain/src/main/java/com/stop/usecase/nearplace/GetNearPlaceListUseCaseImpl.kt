package com.stop.usecase.nearplace

import com.stop.model.nearplace.Place
import com.stop.repository.NearPlaceRepository
import javax.inject.Inject

internal class GetNearPlaceListUseCaseImpl @Inject constructor(
    private val nearPlaceRepository: NearPlaceRepository
): GetNearPlaceListUseCase {

    override suspend fun getNearPlaceList(
        version: Int,
        searchKeyword: String,
        centerLon: Float,
        centerLat: Float,
        appKey: String
    ): List<Place> {
        return nearPlaceRepository.getNearPlaceList(
            version,
            searchKeyword,
            centerLon,
            centerLat,
            appKey
        )
    }

}