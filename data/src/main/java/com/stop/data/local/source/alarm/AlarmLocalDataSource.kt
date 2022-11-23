package com.stop.data.local.source.alarm

import com.stop.data.model.alarm.AlarmRepositoryItem
import kotlinx.coroutines.flow.Flow

interface AlarmLocalDataSource {

    fun insertAlarm(alarmRepositoryItem: AlarmRepositoryItem)

    fun deleteAlarm()

    fun selectAlarm(): Flow<AlarmRepositoryItem>

}