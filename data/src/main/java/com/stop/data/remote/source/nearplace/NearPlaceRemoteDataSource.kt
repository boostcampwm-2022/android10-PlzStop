package com.stop.data.remote.source.nearplace

import com.stop.data.model.nearplace.Place

interface NearPlaceRemoteDataSource {

    suspend fun getNearPlaces(
        version: Int,
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double,
        appKey: String
    ): List<Place>

}