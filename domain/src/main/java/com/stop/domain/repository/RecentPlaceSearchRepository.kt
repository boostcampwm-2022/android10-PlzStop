package com.stop.domain.repository

import com.stop.domain.model.nearplace.PlaceUseCaseItem
import kotlinx.coroutines.flow.Flow

interface RecentPlaceSearchRepository {

    suspend fun insertRecentPlaceSearch(placeUseCaseItem: PlaceUseCaseItem)

    fun getAllRecentPlaceSearch(): Flow<List<PlaceUseCaseItem>>

    suspend fun deleteAllRecentPlaceSearch()

}