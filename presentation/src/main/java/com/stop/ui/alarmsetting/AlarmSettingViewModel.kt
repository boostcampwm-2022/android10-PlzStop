package com.stop.ui.alarmsetting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stop.domain.model.alarm.AlarmUseCaseItem
import com.stop.domain.usecase.alarm.GetAlarmUseCase
import com.stop.domain.usecase.alarm.SaveAlarmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmSettingViewModel @Inject constructor(
    private val saveAlarmUseCase: SaveAlarmUseCase,
    private val getAlarmUseCase: GetAlarmUseCase
) : ViewModel() {

    val alarmTime = MutableLiveData(0)

    //TODO TEST 코드
    private val _alarmUseCaseItem = MutableLiveData<AlarmUseCaseItem?>()
    val alarmUseCaseItem: LiveData<AlarmUseCaseItem?> = _alarmUseCaseItem

    var alarmMethod = true
    var isMission = true

    fun saveAlarm() {
        val alarmUseCaseItem = AlarmUseCaseItem(
            "쑥고개로 2다길 1",
            "현대 아남타워",
            listOf("도보", "버스5517", "신림역", "선릉역", "도보"),
            "23:30",
            alarmTime.value.toString(),
            alarmMethod,
            isMission
        )

        viewModelScope.launch(Dispatchers.IO) {
            saveAlarmUseCase.saveAlarm(alarmUseCaseItem)
        }
    }

    // TODO TEST 코드
    fun getAlarm() {
        viewModelScope.launch(Dispatchers.IO) {
            getAlarmUseCase.getAlarm().collectLatest {
                _alarmUseCaseItem.postValue(it)
            }
        }
    }

}