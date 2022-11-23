package com.stop.ui.alarmsetting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmSettingViewModel @Inject constructor() : ViewModel() {

    val alarmTime = MutableLiveData(0)

}