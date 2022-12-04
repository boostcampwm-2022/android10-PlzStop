package com.stop.data.repository

import com.stop.data.local.source.alarm.RecentPlaceSearchLocalDataSource
import com.stop.data.model.alarm.RecentPlaceSearchItem
import com.stop.domain.model.nearplace.RecentPlaceSearch
import com.stop.domain.repository.RecentPlaceSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecentPlaceSearchRepositoryImpl @Inject constructor(
    private val recentPlaceSearchLocalDataSource: RecentPlaceSearchLocalDataSource
) : RecentPlaceSearchRepository {

    override suspend fun insertRecentPlaceSearch(recentPlaceSearch: RecentPlaceSearch) {
        recentPlaceSearchLocalDataSource.insertRecentPlaceSearch(
            RecentPlaceSearchItem(
                name = recentPlaceSearch.name,
                radius = recentPlaceSearch.radius,
                fullAddressRoad = recentPlaceSearch.fullAddressRoad,
                centerLat = recentPlaceSearch.centerLat,
                centerLon = recentPlaceSearch.centerLon,
            )
        )
    }

    override fun getAllRecentPlaceSearch(): Flow<List<RecentPlaceSearch>> {
        return recentPlaceSearchLocalDataSource.getAllRecentPlaceSearch().map { recentPlaceSearchItems ->
            recentPlaceSearchItems.map { recentPlaceSearchItem ->
                recentPlaceSearchItem.toUseCaseModel()
            }
        }
    }

    override suspend fun deleteAllRecentPlaceSearch() {
        recentPlaceSearchLocalDataSource.deleteAllRecentPlaceSearch()
    }

}