package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.NowStationLocationUseCaseItem

interface GetNowStationLocationUseCase {

    suspend operator fun invoke(
        version: Int,
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double,
        appKey: String
    ): NowStationLocationUseCaseItem

}