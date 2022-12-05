package com.stop.data.remote.source.nearplace

import com.stop.data.model.nearplace.PlaceRepositoryItem

interface NearPlaceRemoteDataSource {

    suspend fun getNearPlaces(
        searchKeyword: String,
        centerLon: Double,
        centerLat: Double
    ): Result<List<PlaceRepositoryItem>>

}