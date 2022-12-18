package com.stop.domain.usecase.alarm

import com.stop.domain.model.alarm.AlarmUseCaseItem
import kotlinx.coroutines.flow.Flow

interface GetAlarmUseCase {

    suspend operator fun invoke() : Flow<AlarmUseCaseItem?>

}