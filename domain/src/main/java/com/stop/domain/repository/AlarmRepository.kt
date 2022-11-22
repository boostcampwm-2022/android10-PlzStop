package com.stop.domain.repository

import com.stop.domain.model.alarm.AlarmUseCaseItem
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    suspend fun insertAlarm(alarmUseCaseItem: AlarmUseCaseItem)

    suspend fun deleteAlarm()

    suspend fun selectAlarm(): Flow<AlarmUseCaseItem>

}
