package com.stop.data.model.alarm

import com.stop.data.local.model.Alarm
import com.stop.domain.model.alarm.AlarmUseCaseItem

data class AlarmRepositoryItem(
    val startPosition: String,
    val endPosition: String,
    val routes: List<String>,
    val lastTime: String, // 막차 시간 -> 23:30:15 시분초
    val alarmTime: Int, // 10분 전 알람 설정 -> 10
    val alarmCode: Int, // 알람을 식별하기 위한 알람 ID
    val alarmMethod: Boolean,
    val isMission: Boolean,
) {

    fun toUseCaseModel() = AlarmUseCaseItem(
        startPosition,
        endPosition,
        routes,
        lastTime,
        alarmTime,
        alarmCode,
        alarmMethod,
        isMission
    )

    fun toDataSourceModel() = Alarm(
        startPosition,
        endPosition,
        routes,
        lastTime,
        alarmTime,
        alarmCode,
        alarmMethod,
        isMission
    )

}
