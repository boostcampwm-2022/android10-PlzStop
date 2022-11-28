package com.stop.ui.map

import com.skt.tmap.TMapPoint
import com.stop.ui.util.Handler

interface MapHandler : Handler {

    fun alertTMapReady()

    fun setOnLocationChangeListener(location: android.location.Location)

    fun setOnDisableScrollWIthZoomLevelListener()

    fun setPanel(tMapPoint: TMapPoint)

}
