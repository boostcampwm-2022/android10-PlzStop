package com.stop.ui.mission

import com.skt.tmap.TMapPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MissionManager {

    var initLocation = MutableStateFlow(TMapPoint())
    var userLocation = MutableStateFlow(TMapPoint())

    private var _userState = MutableStateFlow<UserState>(UserState.Foreground())
    val userState: StateFlow<UserState>
        get() = _userState.asStateFlow()

    fun setLocation(location: TMapPoint) {
        _userState.value = UserState.Foreground(location)
    }

}

sealed interface UserState {

    data class Foreground(val location: TMapPoint = TMapPoint()) : UserState

    data class Background(val locations: List<TMapPoint> = emptyList()) : UserState
}