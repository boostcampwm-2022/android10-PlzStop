package com.stop.domain.usecase.alarm

import com.stop.domain.model.alarm.AlarmUseCaseItem

interface SaveAlarmUseCase {

    suspend operator fun invoke(alarmUseCaseItem: AlarmUseCaseItem)

}