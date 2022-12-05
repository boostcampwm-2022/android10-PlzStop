package com.stop.data.repository

import com.stop.data.local.source.alarm.AlarmLocalDataSource
import com.stop.data.model.alarm.AlarmRepositoryItem
import com.stop.domain.model.alarm.AlarmUseCaseItem
import com.stop.domain.repository.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AlarmRepositoryImpl @Inject constructor(
    private val alarmLocalDataSource: AlarmLocalDataSource
) : AlarmRepository {

    override suspend fun saveAlarm(alarmUseCaseItem: AlarmUseCaseItem) {
        alarmLocalDataSource.saveAlarm(
            AlarmRepositoryItem(
                alarmUseCaseItem.startPosition,
                alarmUseCaseItem.endPosition,
                alarmUseCaseItem.routes,
                alarmUseCaseItem.lastTime,
                alarmUseCaseItem.walkTime,
                alarmUseCaseItem.alarmTime,
                alarmUseCaseItem.alarmCode,
                alarmUseCaseItem.alarmMethod
            )
        )
    }

    override suspend fun deleteAlarm() {
        alarmLocalDataSource.deleteAlarm()
    }


    override suspend fun getAlarm(): Flow<AlarmUseCaseItem?> {
        return alarmLocalDataSource.getAlarm().map { it?.toUseCaseModel() }
    }

}