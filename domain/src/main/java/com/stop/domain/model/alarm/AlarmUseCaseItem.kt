package com.stop.domain.model.alarm

data class AlarmUseCaseItem(
    val startPosition: String,
    val endPosition: String,
    val routes: List<String>,
    val lastTime: String,
    val alarmTime: String,
    val alarmMethod: Boolean,
    val isMission: Boolean,
)
