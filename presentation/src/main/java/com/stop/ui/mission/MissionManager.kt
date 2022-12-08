package com.stop.ui.mission

import com.stop.model.Location
import kotlinx.coroutines.flow.MutableStateFlow

class MissionManager {

    var userLocation = MutableStateFlow(Location(0.0, 0.0))

    var isMissionOver = MutableStateFlow(false)

}