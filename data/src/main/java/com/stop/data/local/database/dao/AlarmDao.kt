package com.stop.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stop.data.local.entity.AlarmEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlarm(alarmEntity: AlarmEntity)

    @Query("DELETE FROM AlarmEntity")
    fun deleteAlarm()

    @Query("SELECT * FROM AlarmEntity")
    fun selectAlarm(): Flow<AlarmEntity>

}