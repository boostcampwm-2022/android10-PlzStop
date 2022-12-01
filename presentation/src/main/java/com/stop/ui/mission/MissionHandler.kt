package com.stop.ui.mission

import com.skt.tmap.TMapPoint
import com.stop.ui.util.Handler

interface MissionHandler : Handler {

    fun alertTMapReady()

    fun setOnLocationChangeListener(nowLocation: TMapPoint, beforeLocation: TMapPoint, canMakeLine: Boolean)

    fun setOnEnableScrollWithZoomLevelListener()

}
