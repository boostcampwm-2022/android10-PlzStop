package com.stop.data.local.model

import com.squareup.moshi.JsonClass
import com.stop.data.model.alarm.AlarmRepositoryItem

@JsonClass(generateAdapter = true)
data class Alarm(
    val startPosition: String,
    val endPosition: String,
    val routes: List<String>,
    val lastTime: String,
    val alarmTime: String,
    val alarmMethod: Boolean,
    val isMission: Boolean,
) {

    fun toRepositoryModel() = AlarmRepositoryItem(
        startPosition,
        endPosition,
        routes,
        lastTime,
        alarmTime,
        alarmMethod,
        isMission
    )


}