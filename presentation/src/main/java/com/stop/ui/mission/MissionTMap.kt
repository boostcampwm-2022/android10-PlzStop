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

    private var lineNum = 0

    fun drawMoveLine(nowLocation: TMapPoint, beforeLocation: TMapPoint) {
        val points = arrayListOf(nowLocation, beforeLocation)
        val polyLine = TMapPolyLine(PERSON_LINE + lineNum.toString(), points).apply {
            lineColor = LINE_COLOR
            outLineColor = LINE_COLOR
        }
        lineNum += 1
        tMapView.addTMapPolyLine(polyLine)
    }

    fun moveLocation(longitude: String, latitude: String) {
        val mockLocation = TMapPoint(latitude.toDouble(), longitude.toDouble())

        drawMoveLine(mockLocation, tMapView.locationPoint)
        makeMarker(PERSON_MARKER, PERSON_MARKER_IMG, mockLocation)
        tMapView.setLocationPoint(mockLocation.latitude, mockLocation.longitude)
    }

    fun setEnableScroll() {
        tMapView.setOnEnableScrollWithZoomLevelListener { _, _ ->
            isTracking = false
        }
    }


    companion object {
        private const val PERSON_MARKER = "marker_person_pin"
        private const val PERSON_MARKER_IMG = R.drawable.ic_person_pin

        private const val PERSON_LINE = "person_line"
        private const val LINE_COLOR = Color.MAGENTA
    }

}