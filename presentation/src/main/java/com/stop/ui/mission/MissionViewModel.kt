package com.stop.ui.mission

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.domain.model.route.tmap.custom.Place
import com.stop.model.map.Location
import com.stop.model.mission.MissionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MissionViewModel @Inject constructor() : ViewModel() {

    val destination = MutableStateFlow(Place("null", Coordinate("37.553836", "126.969652")))
    val missionStatus = MutableStateFlow(MissionStatus.BEFORE)
    val lastTime = MutableLiveData<String>()
    val userLocations = MutableStateFlow(arrayListOf(Location(37.553836, 126.969652)))

}
