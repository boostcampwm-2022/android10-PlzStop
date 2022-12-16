package com.stop.domain.usecase.alarm

import com.stop.domain.model.alarm.AlarmUseCaseItem
import com.stop.domain.repository.AlarmRepository
import javax.inject.Inject

class SaveAlarmUseCaseImpl @Inject constructor(
    private val alarmRepository: AlarmRepository
) : SaveAlarmUseCase {

    override suspend operator fun invoke(alarmUseCaseItem: AlarmUseCaseItem) {
        alarmRepository.saveAlarm(alarmUseCaseItem)
    }

}