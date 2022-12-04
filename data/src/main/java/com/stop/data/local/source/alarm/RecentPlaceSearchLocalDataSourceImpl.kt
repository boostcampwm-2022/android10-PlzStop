package com.stop.data.local.source.alarm

import com.stop.data.local.database.dao.RecentPlaceSearchDao
import com.stop.data.model.alarm.RecentPlaceSearchItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecentPlaceSearchLocalDataSourceImpl @Inject constructor(
    private val recentPlaceSearchDao: RecentPlaceSearchDao
) : RecentPlaceSearchLocalDataSource {

    override suspend fun insertRecentPlaceSearch(recentPlaceSearchItem: RecentPlaceSearchItem) {
        recentPlaceSearchDao.insertRecentPlaceSearchEntity(recentPlaceSearchItem.toDataSourceModel())
    }

    override fun getAllRecentPlaceSearch(): Flow<List<RecentPlaceSearchItem>> {
        return recentPlaceSearchDao.getAllRecentPlaceSearchEntity().map { recentPlaceSearchEntities ->
            recentPlaceSearchEntities.map { recentPlaceSearchEntity ->
                recentPlaceSearchEntity.toRepositoryModel()
            }
        }
    }

    override suspend fun deleteAllRecentPlaceSearch() {
        recentPlaceSearchDao.deleteAllRecentPlaceSearchEntity()
    }

}