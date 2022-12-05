package com.stop.domain.usecase.nearplace

import com.stop.domain.model.nearplace.PlaceUseCaseItem
import com.stop.domain.repository.RecentPlaceSearchRepository
import javax.inject.Inject

class InsertRecentPlaceSearchUseCaseImpl @Inject constructor(
    private val recentPlaceSearchRepository: RecentPlaceSearchRepository
) : InsertRecentPlaceSearchUseCase {

    override suspend fun insertRecentPlaceSearch(placeUseCaseItem: PlaceUseCaseItem) {
        recentPlaceSearchRepository.insertRecentPlaceSearch(placeUseCaseItem)
    }

}