package com.stop.data.local.source.alarm

import com.stop.data.model.nearplace.PlaceRepositoryItem
import kotlinx.coroutines.flow.Flow

interface RecentPlaceSearchLocalDataSource {

    suspend fun insertRecentPlaceSearch(placeRepositoryItem: PlaceRepositoryItem)

    fun getAllRecentPlaceSearch(): Flow<List<PlaceRepositoryItem>>

    suspend fun deleteAllRecentPlaceSearch()

}