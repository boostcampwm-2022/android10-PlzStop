package com.stop.domain.usecase.nearplace

import com.stop.domain.model.nearplace.Place
import com.stop.domain.repository.NearPlaceRepository
import com.stop.domain.usecase.nearplace.GetNearPlaceListUseCase
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