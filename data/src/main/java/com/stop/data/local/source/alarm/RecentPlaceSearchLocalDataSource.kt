package com.stop.data.local.source.alarm

import com.stop.data.model.alarm.RecentPlaceSearchItem
import kotlinx.coroutines.flow.Flow

interface RecentPlaceSearchLocalDataSource {

    suspend fun insertRecentPlaceSearch(recentPlaceSearchItem: RecentPlaceSearchItem)

    fun getAllRecentPlaceSearch(): Flow<List<RecentPlaceSearchItem>>

    suspend fun deleteAllRecentPlaceSearch()

}