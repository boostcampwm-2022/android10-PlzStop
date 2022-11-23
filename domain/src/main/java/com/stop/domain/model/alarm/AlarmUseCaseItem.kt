package com.stop.domain.model.alarm

data class AlarmUseCaseItem(
    val routeInfo: String,
    val transportInfo: String,
    val lastTime: String,
    val alarmTime: String,
    val walkTime: String,
    val alarmMethod: Boolean, // 소리 : true, 진동 : false
    val isMission: Boolean
)
