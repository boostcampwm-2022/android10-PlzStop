package com.stop.data.local.model

import com.squareup.moshi.JsonClass
import com.stop.data.model.alarm.AlarmRepositoryItem
import com.stop.domain.model.route.tmap.custom.Route

@JsonClass(generateAdapter = true)
data class Alarm(
    val startPosition: String,
    val endPosition: String,
    val routes: Route,
    val lastTime: String, // 막차 시간 -> 23:30:15 시분초
    val walkTime : Int, // 도보시간 -> 분단위
    val alarmTime: Int, // 10분 전 알람 설정 -> 10
    val alarmCode: Int, // 알람을 식별하기 위한 알람 ID
    val alarmMethod: Boolean, // true 소리 false 진동
) {

    fun toRepositoryModel() = AlarmRepositoryItem(
        startPosition,
        endPosition,
        routes,
        lastTime,
        walkTime,
        alarmTime,
        alarmCode,
        alarmMethod
    )

}