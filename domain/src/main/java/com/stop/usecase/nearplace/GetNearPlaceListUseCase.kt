package com.stop.usecase.nearplace

import com.stop.model.nearplace.Place

interface GetNearPlaceListUseCase {

    suspend fun getNearPlaceList(
        version: Int,
        searchKeyword: String,
        centerLon: Float,
        centerLat: Float,
        appKey: String
    ) : List<Place>

}