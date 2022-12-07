package com.stop.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stop.data.local.model.RecentPlaceSearchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentPlaceSearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentPlaceSearchEntity(recentPlaceSearchEntity: RecentPlaceSearchEntity)

    @Query("SELECT * FROM RecentPlaceSearchEntity ORDER BY id DESC")
    fun getAllRecentPlaceSearchEntity(): Flow<List<RecentPlaceSearchEntity>>

    @Query("DELETE FROM RecentPlaceSearchEntity")
    suspend fun deleteAllRecentPlaceSearchEntity()
}