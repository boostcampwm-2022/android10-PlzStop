package com.stop.domain.usecase.alarm

import com.stop.domain.repository.AlarmRepository
import javax.inject.Inject

class DeleteAlarmUseCaseImpl @Inject constructor(
    private val alarmRepository: AlarmRepository
) : DeleteAlarmUseCase{

    override fun deleteAlarm() {
        alarmRepository.deleteAlarm()
    }

}