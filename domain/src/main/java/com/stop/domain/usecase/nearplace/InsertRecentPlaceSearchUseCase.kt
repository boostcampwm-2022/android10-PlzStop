package com.stop.domain.usecase.nearplace

import com.stop.domain.model.nearplace.PlaceUseCaseItem

interface InsertRecentPlaceSearchUseCase {

    suspend fun insertRecentPlaceSearch(placeUseCaseItem: PlaceUseCaseItem)

}