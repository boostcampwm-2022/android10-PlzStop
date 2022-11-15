package com.stop.domain.usecase.nearplace

import com.stop.domain.model.nearplace.Place

interface GetNearPlaceListUseCase {

    suspend fun getNearPlaceList(
        version: Int,
        searchKeyword: String,
        centerLon: Float,
        centerLat: Float,
        appKey: String
    ) : List<Place>

}