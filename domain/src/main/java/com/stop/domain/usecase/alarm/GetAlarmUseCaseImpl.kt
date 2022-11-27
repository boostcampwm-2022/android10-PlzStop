package com.stop.domain.usecase.alarm

import com.stop.domain.model.alarm.AlarmUseCaseItem
import com.stop.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlarmUseCaseImpl @Inject constructor(
    private val alarmRepository: AlarmRepository
) : GetAlarmUseCase{

    override suspend fun getAlarm(): Flow<AlarmUseCaseItem?> {
        return alarmRepository.getAlarm()
    }

}