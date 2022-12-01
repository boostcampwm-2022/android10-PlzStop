package com.stop.ui.mission

import android.content.Context
import com.skt.tmap.TMapPoint
import com.skt.tmap.overlay.TMapPolyLine
import com.stop.ui.util.Marker
import com.stop.ui.util.TMap

class MissionTMap(
    context: Context,
    handler: MissionHandler,
) : TMap(context, handler) {

    fun drawMoveLine(nowLocation: TMapPoint, beforeLocation: TMapPoint, id: String, color: Int) {
        val points = arrayListOf(nowLocation, beforeLocation)
        val polyLine = TMapPolyLine(id, points).apply {
            lineColor = color
            outLineColor = color
        }
        tMapView.addTMapPolyLine(polyLine)
    }

    fun moveLocation(longitude: String, latitude: String) {
        val mockLocation = TMapPoint(latitude.toDouble(), longitude.toDouble())

        drawMoveLine(
            mockLocation,
            tMapView.locationPoint,
            Marker.PERSON_LINE + PERSON_LINE_NUM.toString(),
            Marker.PERSON_LINE_COLOR
        )
        PERSON_LINE_NUM += 1

        addMarker(Marker.PERSON_MARKER, Marker.PERSON_MARKER_IMG, mockLocation)
        tMapView.setLocationPoint(mockLocation.latitude, mockLocation.longitude)
    }

    companion object {
        private var PERSON_LINE_NUM = 0
    }

}