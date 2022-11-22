package com.stop.domain.usecase.alarm

import com.stop.domain.model.alarm.AlarmUseCaseItem

interface InsertAlarmUseCase {

    suspend fun insertAlarm(alarmUseCaseItem: AlarmUseCaseItem)

}