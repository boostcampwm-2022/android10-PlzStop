package com.stop.domain.repository

import com.stop.domain.model.alarm.AlarmUseCaseItem
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {

    fun insertAlarm(alarmUseCaseItem: AlarmUseCaseItem)

    fun deleteAlarm()

    fun selectAlarm(): Flow<AlarmUseCaseItem>

}
