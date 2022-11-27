package com.stop.ui.map

import android.content.Context
import com.stop.R
import com.stop.model.Location
import com.stop.ui.util.TMap

class MapTMap(
    context: Context,
    private val handler: MapHandler,
) : TMap(context, handler) {

    fun clickMap() {
        val enablePoint = mutableSetOf<Location>()
        tMapView.setOnEnableScrollWithZoomLevelListener { _, centerPoint ->
            enablePoint.add(Location(centerPoint.latitude, centerPoint.longitude))
            isTracking = false
        }

        tMapView.setOnDisableScrollWithZoomLevelListener { _, _ ->
            if (enablePoint.size == SAME_POINT) {
                handler.setOnDisableScrollWIthZoomLevelListener()
            }
            enablePoint.clear()
        }
    }

    fun clickLocation() {
        tMapView.setOnLongClickListenerCallback { _, _, tMapPoint ->
            makeMarker(
                PLACE_MARKER,
                PLACE_MARKER_IMG,
                tMapPoint
            )

            tMapView.setCenterPoint(tMapPoint.latitude, tMapPoint.longitude, true)
            handler.setPanel(tMapPoint)
        }
    }

    companion object {
        private const val PLACE_MARKER = "marker"
        private const val PLACE_MARKER_IMG = R.drawable.ic_baseline_location_on_32

        private const val SAME_POINT = 1
    }
}