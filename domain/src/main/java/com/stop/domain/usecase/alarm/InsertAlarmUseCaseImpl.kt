package com.stop.domain.usecase.alarm

import com.stop.domain.model.alarm.AlarmUseCaseItem
import com.stop.domain.repository.AlarmRepository
import javax.inject.Inject

class InsertAlarmUseCaseImpl @Inject constructor(
    private val alarmRepository: AlarmRepository
) : InsertAlarmUseCase{

    override suspend fun insertAlarm(alarmUseCaseItem: AlarmUseCaseItem) {
        alarmRepository.insertAlarm(alarmUseCaseItem)
    }

}