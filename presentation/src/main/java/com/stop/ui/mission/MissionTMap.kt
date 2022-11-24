package com.stop.ui.mission

import android.content.Context
import android.graphics.Color
import com.skt.tmap.TMapPoint
import com.skt.tmap.overlay.TMapPolyLine
import com.stop.R
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
            PERSON_LINE + PERSON_LINE_NUM.toString(),
            PERSON_LINE_COLOR
        )
        PERSON_LINE_NUM += 1

        makeMarker(PERSON_MARKER, PERSON_MARKER_IMG, mockLocation)
        tMapView.setLocationPoint(mockLocation.latitude, mockLocation.longitude)
    }

    companion object {
        private const val PERSON_MARKER = "marker_person_pin"
        private const val PERSON_MARKER_IMG = R.drawable.ic_person_pin

        private const val PERSON_LINE = "person_line"
        private const val PERSON_LINE_COLOR = Color.MAGENTA
        private var PERSON_LINE_NUM = 0
    }

}