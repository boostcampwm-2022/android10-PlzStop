package com.stop.domain.usecase.nearplace

import com.stop.domain.model.nearplace.PlaceUseCaseItem
import kotlinx.coroutines.flow.Flow

interface GetRecentPlaceSearchUseCase {

    fun getAllRecentPlaceSearch(): Flow<List<PlaceUseCaseItem>>

}