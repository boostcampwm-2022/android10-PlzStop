package com.stop.data.local.source.alarm

import com.stop.data.local.database.dao.RecentPlaceSearchDao
import com.stop.data.model.nearplace.PlaceRepositoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecentPlaceSearchLocalDataSourceImpl @Inject constructor(
    private val recentPlaceSearchDao: RecentPlaceSearchDao
) : RecentPlaceSearchLocalDataSource {

    override suspend fun insertRecentPlaceSearch(placeRepositoryItem: PlaceRepositoryItem) {
        recentPlaceSearchDao.insertRecentPlaceSearchEntity(placeRepositoryItem.toDataSourceModel())
    }

    override fun getAllRecentPlaceSearch(): Flow<List<PlaceRepositoryItem>> {
        return recentPlaceSearchDao.getAllRecentPlaceSearchEntity().map { placeEntities ->
            placeEntities.map { placeEntity ->
                placeEntity.toRepositoryModel()
            }
        }
    }

    override suspend fun deleteAllRecentPlaceSearch() {
        recentPlaceSearchDao.deleteAllRecentPlaceSearchEntity()
    }

}