package com.stop.data.model.alarm

import com.stop.data.local.entity.AlarmEntity
import com.stop.domain.model.alarm.AlarmUseCaseItem

data class AlarmRepositoryItem(
    val routeInfo: String,
    val transportInfo: String,
    val lastTime: String,
    val alarmTime: String,
    val walkTime: String,
    val alarmMethod: Boolean, // 소리 : true, 진동 : false
    val isMission: Boolean
) {

    fun toUseCaseModel() = AlarmUseCaseItem(
        routeInfo,
        transportInfo,
        lastTime,
        alarmTime,
        walkTime,
        alarmMethod,
        isMission
    )

    fun toEntity() = AlarmEntity(
        DEFAULT_ID,
        routeInfo,
        transportInfo,
        lastTime,
        alarmTime,
        walkTime,
        alarmMethod,
        isMission
    )

    companion object {
        private const val DEFAULT_ID = 1L
    }

}
