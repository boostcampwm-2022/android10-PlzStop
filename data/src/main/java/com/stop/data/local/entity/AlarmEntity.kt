package com.stop.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stop.data.model.alarm.AlarmRepositoryItem

@Entity
data class AlarmEntity(
    @PrimaryKey
    val id: Long,
    val routeInfo: String,
    val transportInfo: String,
    val lastTime: String,
    val alarmTime: String,
    val walkTime: String,
    val alarmMethod: Boolean, // 소리 : true, 진동 : false
    val isMission: Boolean
) {

    fun toRepositoryModel() = AlarmRepositoryItem(
        routeInfo,
        transportInfo,
        lastTime,
        alarmTime,
        walkTime,
        alarmMethod,
        isMission
    )

}