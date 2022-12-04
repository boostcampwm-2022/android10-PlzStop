package com.stop.ui.alarmsetting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stop.domain.model.alarm.AlarmUseCaseItem
import com.stop.domain.usecase.alarm.GetAlarmUseCase
import com.stop.domain.usecase.alarm.SaveAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmSettingViewModel @Inject constructor(
    private val saveAlarmUseCase: SaveAlarmUseCase,
    private val getAlarmUseCase: GetAlarmUseCase
) : ViewModel() {

    val alarmTime = MutableLiveData(0)
    var alarmMethod = true

    private val _alarmItem = MutableStateFlow<AlarmUseCaseItem?>(null)
    val alarmItem: StateFlow<AlarmUseCaseItem?> = _alarmItem

    private val _isAlarmItemNotNull = MutableStateFlow(false)
    val isAlarmItemNotNull: StateFlow<Boolean> = _isAlarmItemNotNull

    fun saveAlarm(alarmUseCaseItem: AlarmUseCaseItem) {
        viewModelScope.launch(Dispatchers.IO) {
            saveAlarmUseCase.saveAlarm(alarmUseCaseItem.copy(alarmTime = alarmTime.value ?: 0, alarmMethod = alarmMethod))
        }
    }

    fun getAlarm() {
        viewModelScope.launch(Dispatchers.IO) {
            getAlarmUseCase.getAlarm().collectLatest {
                _alarmItem.value = it

                _isAlarmItemNotNull.value = it != null
            }
        }
    }

}