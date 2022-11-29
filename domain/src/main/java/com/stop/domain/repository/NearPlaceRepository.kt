package com.stop.domain.repository

import com.stop.domain.model.nearplace.Place
import com.stop.domain.model.nowlocation.NowStationLocationUseCaseItem

interface NearPlaceRepository {

    suspend fun getNearPlaces(
        version: Int,
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double,
        appKey: String
    ): List<Place>

    suspend fun getNowStationLocationInfo(
        version: Int,
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double,
        appKey: String
    ): NowStationLocationUseCaseItem

}