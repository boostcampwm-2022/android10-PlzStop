package com.stop.domain.repository

import com.stop.domain.model.nearplace.Place

interface NearPlaceRepository {

    suspend fun getNearPlaces(
        version: Int,
        searchKeyword: String,
        centerLon: Float,
        centerLat: Float,
        appKey: String
    ): List<Place>

}