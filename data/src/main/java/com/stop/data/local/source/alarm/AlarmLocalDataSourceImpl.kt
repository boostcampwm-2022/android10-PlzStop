package com.stop.data.local.source.alarm

import com.stop.data.local.database.dao.AlarmDao
import com.stop.data.model.alarm.AlarmRepositoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AlarmLocalDataSourceImpl @Inject constructor(
    private val alarmDao: AlarmDao
) : AlarmLocalDataSource {

    override suspend fun insertAlarm(alarmRepositoryItem: AlarmRepositoryItem) {
        alarmDao.insertAlarm(alarmRepositoryItem.toEntity())
    }

    override suspend fun deleteAlarm() {
        alarmDao.deleteAlarm()
    }

    override suspend fun selectAlarm(): Flow<AlarmRepositoryItem> {
        return alarmDao.selectAlarm().map { it.toRepositoryModel() }
    }

}