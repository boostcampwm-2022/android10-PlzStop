package com.stop.domain.usecase.alarm

import com.stop.domain.model.alarm.AlarmUseCaseItem

interface InsertAlarmUseCase {

    fun insertAlarm(alarmUseCaseItem: AlarmUseCaseItem)

}