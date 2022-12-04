package com.stop.data.repository

import com.stop.data.local.source.alarm.RecentPlaceSearchLocalDataSource
import com.stop.data.model.nearplace.PlaceRepositoryItem
import com.stop.domain.model.nearplace.PlaceUseCaseItem
import com.stop.domain.repository.RecentPlaceSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecentPlaceSearchRepositoryImpl @Inject constructor(
    private val recentPlaceSearchLocalDataSource: RecentPlaceSearchLocalDataSource
) : RecentPlaceSearchRepository {

    override suspend fun insertRecentPlaceSearch(placeUseCaseItem: PlaceUseCaseItem) {
        recentPlaceSearchLocalDataSource.insertRecentPlaceSearch(
            PlaceRepositoryItem(
                name = placeUseCaseItem.name,
                radius = placeUseCaseItem.radius,
                fullAddressRoad = placeUseCaseItem.fullAddressRoad,
                centerLat = placeUseCaseItem.centerLat,
                centerLon = placeUseCaseItem.centerLon,
            )
        )
    }

    override fun getAllRecentPlaceSearch(): Flow<List<PlaceUseCaseItem>> {
        return recentPlaceSearchLocalDataSource.getAllRecentPlaceSearch().map { placeRepositoryItems ->
            placeRepositoryItems.map { placeRepositoryItem ->
                placeRepositoryItem.toUseCaseModel()
            }
        }
    }

    override suspend fun deleteAllRecentPlaceSearch() {
        recentPlaceSearchLocalDataSource.deleteAllRecentPlaceSearch()
    }

}