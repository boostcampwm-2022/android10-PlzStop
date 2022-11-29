package com.stop.domain.model.alarm

data class AlarmUseCaseItem(
    val startPosition: String,
    val endPosition: String,
    val routes: List<String>,
    val lastTime: String, // 막차 시간 -> 23:30:15 시분초
    val alarmTime: Int, // 10분 전 알람 설정 -> 10
    val alarmCode: Int, // 알람을 식별하기 위한 알람 ID
    val alarmMethod: Boolean,
    val isMission: Boolean,
)
