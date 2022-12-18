package com.stop.domain.usecase.nearplace

import com.stop.domain.repository.RecentPlaceSearchRepository
import javax.inject.Inject

class DeleteRecentPlaceSearchUseCaseImpl @Inject constructor(
    private val recentPlaceSearchRepository: RecentPlaceSearchRepository
) : DeleteRecentPlaceSearchUseCase {

    override suspend operator fun invoke() {
        recentPlaceSearchRepository.deleteAllRecentPlaceSearch()
    }

}