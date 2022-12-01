package com.stop.ui.map

import android.content.Context
import android.graphics.PointF
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.TMapView.OnClickListenerCallback
import com.skt.tmap.overlay.TMapMarkerItem
import com.skt.tmap.poi.TMapPOIItem
import com.stop.model.Location
import com.stop.ui.util.Marker
import com.stop.ui.util.TMap
import java.util.ArrayList

class MapTMap(
    context: Context,
    private val handler: MapHandler,
) : TMap(context, handler) {

    private val enablePoints = mutableSetOf<Location>()
    private var isLongClick = false

    fun initListener() {
        tMapView.setOnClickListenerCallback(onClickListenerCallback)
        tMapView.setOnLongClickListenerCallback(onLongClickListenerCallback)
        tMapView.setOnEnableScrollWithZoomLevelListener(onEnableScrollWithZoomLevelCallback)
    }

    private val onClickListenerCallback = object : OnClickListenerCallback {
        override fun onPressDown(
            p0: ArrayList<TMapMarkerItem>?,
            p1: ArrayList<TMapPOIItem>?,
            p2: TMapPoint?,
            p3: PointF?
        ) {
            isLongClick = false
        }

        override fun onPressUp(
            p0: ArrayList<TMapMarkerItem>?,
            p1: ArrayList<TMapPOIItem>?,
            p2: TMapPoint?,
            p3: PointF?
        ) {
            if (enablePoints.size < SCROLL_NUM && isLongClick.not()) {
                handler.setOnDisableScrollWIthZoomLevelListener()
            }

            enablePoints.clear()
        }
    }

    private val onLongClickListenerCallback =
        TMapView.OnLongClickListenerCallBack { _, _, tMapPoint ->
            isLongClick = true
            tMapView.setCenterPoint(tMapPoint.latitude, tMapPoint.longitude, true)
            addMarker(Marker.PLACE_MARKER, Marker.PLACE_MARKER_IMG, tMapPoint)
            handler.setPanel(tMapPoint)
        }

    private val onEnableScrollWithZoomLevelCallback =
        TMapView.OnEnableScrollWithZoomLevelCallback { _, tMapPoint ->
            isTracking = false
            enablePoints.add(Location(tMapPoint.latitude, tMapPoint.longitude))
        }

    companion object {
        private const val SCROLL_NUM = 3
    }
}