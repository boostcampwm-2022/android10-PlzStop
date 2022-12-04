package com.stop.domain.usecase.nearplace

import com.stop.domain.model.nearplace.RecentPlaceSearch
import com.stop.domain.repository.RecentPlaceSearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentPlaceSearchUseCaseImpl @Inject constructor(
    private val recentPlaceSearchRepository: RecentPlaceSearchRepository
) : GetRecentPlaceSearchUseCase {

    override fun getAllRecentPlaceSearch(): Flow<List<RecentPlaceSearch>> {
        return recentPlaceSearchRepository.getAllRecentPlaceSearch()
    }

}