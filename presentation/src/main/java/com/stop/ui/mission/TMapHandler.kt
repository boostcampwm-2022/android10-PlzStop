package com.stop.ui.mission

import com.skt.tmap.TMapPoint

interface TMapHandler {

    fun alertTMapReady()

    fun setOnLocationChangeListener(nowLocation: TMapPoint, beforeLocation: TMapPoint)
}
