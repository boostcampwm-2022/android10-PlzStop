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

    override suspend fun insertAlarm(alarmUseCaseItem: AlarmUseCaseItem) {
        alarmLocalDataSource.insertAlarm(
            AlarmRepositoryItem(
                alarmUseCaseItem.routeInfo,
                alarmUseCaseItem.transportInfo,
                alarmUseCaseItem.lastTime,
                alarmUseCaseItem.alarmTime,
                alarmUseCaseItem.walkTime,
                alarmUseCaseItem.alarmMethod,
                alarmUseCaseItem.isMission
            )
        )
    }

    override suspend fun deleteAlarm() {
        alarmLocalDataSource.deleteAlarm()
    }

    override suspend fun selectAlarm(): Flow<AlarmUseCaseItem> {
        return alarmLocalDataSource.selectAlarm().map { it.toUseCaseModel() }
    }

}