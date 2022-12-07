package com.stop.domain.repository

import com.stop.domain.model.nearplace.PlaceUseCaseItem
import com.stop.domain.model.nowlocation.NowStationLocationUseCaseItem

interface NearPlaceRepository {

    suspend fun getNearPlaces(
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double
    ): List<PlaceUseCaseItem>

    suspend fun getNowStationLocationInfo(
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double
    ): NowStationLocationUseCaseItem

}