package com.stop.data.model.alarm

import com.stop.data.local.model.Alarm
import com.stop.domain.model.alarm.AlarmUseCaseItem

data class AlarmRepositoryItem(
    val startPosition: String,
    val endPosition: String,
    val routes: List<String>,
    val lastTime: String,
    val alarmTime: String,
    val alarmMethod: Boolean,
    val isMission: Boolean,
) {

    fun toUseCaseModel() = AlarmUseCaseItem(
        startPosition,
        endPosition,
        routes,
        lastTime,
        alarmTime,
        alarmMethod,
        isMission
    )

    fun toDataSourceModel() = Alarm(
        startPosition,
        endPosition,
        routes,
        lastTime,
        alarmTime,
        alarmMethod,
        isMission
    )

}
