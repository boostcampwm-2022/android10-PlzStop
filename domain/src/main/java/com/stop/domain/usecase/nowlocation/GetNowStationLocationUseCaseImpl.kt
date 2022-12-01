package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.NowStationLocationUseCaseItem
import com.stop.domain.repository.NearPlaceRepository
import javax.inject.Inject

class GetNowStationLocationUseCaseImpl @Inject constructor(
    private val nearPlaceRepository: NearPlaceRepository
) : GetNowStationLocationUseCase {

    override suspend fun invoke(
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double
    ): NowStationLocationUseCaseItem {
        return nearPlaceRepository.getNowStationLocationInfo(
            searchKeyword = searchKeyword,
            centerLon = centerLon,
            centerLat = centerLat
        )
    }

}