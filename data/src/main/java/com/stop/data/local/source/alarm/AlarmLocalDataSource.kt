package com.stop.data.local.source.alarm

import com.stop.data.model.alarm.AlarmRepositoryItem
import kotlinx.coroutines.flow.Flow

interface AlarmLocalDataSource {

    suspend fun saveAlarm(alarmRepositoryItem: AlarmRepositoryItem)

    suspend fun deleteAlarm()

    suspend fun getAlarm(): Flow<AlarmRepositoryItem?>

}