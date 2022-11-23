package com.stop.data.local.source.alarm

import com.stop.data.model.alarm.AlarmRepositoryItem
import kotlinx.coroutines.flow.Flow

interface AlarmLocalDataSource {

    suspend fun insertAlarm(alarmRepositoryItem: AlarmRepositoryItem)

    suspend fun deleteAlarm()

    fun selectAlarm(): Flow<AlarmRepositoryItem>

}