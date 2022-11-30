package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.NowStationLocationUseCaseItem

interface GetNowStationLocationUseCase {

    suspend operator fun invoke(
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double
    ): NowStationLocationUseCaseItem

}