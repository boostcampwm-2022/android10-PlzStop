package com.stop.data.local.database.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stop.data.local.entity.AlarmEntity

@Database(
    entities = [AlarmEntity::class],
    version = 1
)
abstract class TroubleShooterApplicationDatabase : RoomDatabase() {

    abstract fun getAlarmDao(): AlarmDao

    companion object {
        const val DB_NAME = "TroubleShooter.db"
    }
}