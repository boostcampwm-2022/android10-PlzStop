package com.stop.domain.repository

import com.stop.domain.model.nearplace.RecentPlaceSearch
import kotlinx.coroutines.flow.Flow

interface RecentPlaceSearchRepository {

    suspend fun insertRecentPlaceSearch(recentPlaceSearch: RecentPlaceSearch)

    fun getAllRecentPlaceSearch(): Flow<List<RecentPlaceSearch>>

    suspend fun deleteAllRecentPlaceSearch()

}