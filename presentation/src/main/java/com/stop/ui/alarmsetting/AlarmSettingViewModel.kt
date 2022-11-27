package com.stop.ui.alarmsetting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stop.domain.model.alarm.AlarmUseCaseItem
import com.stop.domain.usecase.alarm.GetAlarmUseCase
import com.stop.domain.usecase.alarm.SaveAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmSettingViewModel @Inject constructor(
    private val saveAlarmUseCase: SaveAlarmUseCase,
    private val getAlarmUseCase: GetAlarmUseCase
) : ViewModel() {

    val alarmTime = MutableLiveData(0)

    suspend fun save(alarmUseCaseItem: AlarmUseCaseItem){
        saveAlarmUseCase.saveAlarm(alarmUseCaseItem)
    }

    suspend fun get()  =
        getAlarmUseCase.getAlarm()

}