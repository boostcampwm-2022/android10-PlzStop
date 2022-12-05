package com.stop.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stop.data.local.database.dao.RecentPlaceSearchDao
import com.stop.data.local.model.RecentPlaceSearchEntity

@Database(
    entities = [RecentPlaceSearchEntity::class],
    version = 1
)
abstract class TroubleShooterApplicationDatabase : RoomDatabase() {

    abstract fun getRecentPlaceSearchDao(): RecentPlaceSearchDao

    companion object {
        const val DB_NAME = "TroubleShooter.db"
    }
}
