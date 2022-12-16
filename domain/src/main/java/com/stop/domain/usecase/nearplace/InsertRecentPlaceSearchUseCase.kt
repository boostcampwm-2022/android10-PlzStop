package com.stop.domain.usecase.nearplace

import com.stop.domain.model.nearplace.PlaceUseCaseItem

interface InsertRecentPlaceSearchUseCase {

    suspend operator fun invoke(placeUseCaseItem: PlaceUseCaseItem)

}