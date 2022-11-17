package com.stop.domain.repository

import com.stop.domain.model.nearplace.Place
import kotlinx.coroutines.flow.Flow

interface NearPlaceRepository {

    suspend fun getNearPlaces(
        version: Int,
        searchKeyword: String,
        centerLon: Float,
        centerLat: Float,
        appKey: String
    ): Flow<List<Place>>

}