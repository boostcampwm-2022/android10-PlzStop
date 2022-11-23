package com.stop.domain.repository

import com.stop.domain.model.nearplace.Place
import kotlinx.coroutines.flow.Flow

interface NearPlaceRepository {

    suspend fun getNearPlaces(
        version: Int,
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double,
        appKey: String
    ): Flow<List<Place>>

}