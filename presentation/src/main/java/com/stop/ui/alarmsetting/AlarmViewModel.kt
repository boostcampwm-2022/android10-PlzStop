package com.stop.ui.alarmsetting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlarmViewModel : ViewModel() {
    private val _fakeAlarmUseCaseItem = MutableLiveData<AlarmUseCaseItem>()
    val fakeAlarmUseCaseItem: LiveData<AlarmUseCaseItem> = _fakeAlarmUseCaseItem

    init {
        _fakeAlarmUseCaseItem.value = AlarmUseCaseItem(
            startPosition = "성복역 신분당선",
            endPosition = "이엔씨벤처드림타워3차",
            routes = listOf("성복역 승차", "강남역 환승", "구로디지털단지역 하차"),
            lastTime = "23시 50분",
            alarmTime = "23시 30분",
            alarmMethod = true,
            isMission = true
        )
    }
}

data class AlarmUseCaseItem(
    val startPosition: String,
    val endPosition: String,
    val routes: List<String>,
    val lastTime: String,
    val alarmTime: String,
    val alarmMethod: Boolean,
    val isMission: Boolean
)