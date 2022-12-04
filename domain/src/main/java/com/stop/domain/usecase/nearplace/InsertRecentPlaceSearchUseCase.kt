package com.stop.domain.usecase.nearplace

import com.stop.domain.model.nearplace.RecentPlaceSearch

interface InsertRecentPlaceSearchUseCase {

    suspend fun insertRecentPlaceSearch(recentPlaceSearch: RecentPlaceSearch)

}