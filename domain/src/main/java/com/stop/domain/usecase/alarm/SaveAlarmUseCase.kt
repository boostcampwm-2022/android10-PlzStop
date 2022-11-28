package com.stop.domain.usecase.alarm

import com.stop.domain.model.alarm.AlarmUseCaseItem

interface SaveAlarmUseCase {

    suspend fun saveAlarm(alarmUseCaseItem: AlarmUseCaseItem)

}