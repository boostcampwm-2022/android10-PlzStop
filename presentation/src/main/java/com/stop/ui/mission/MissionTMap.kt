package com.stop.ui.mission

import android.content.Context
import android.util.Log
import com.skt.tmap.TMapPoint
import com.skt.tmap.overlay.TMapPolyLine
import com.stop.ui.util.Marker
import com.stop.ui.util.TMap

class MissionTMap(
    context: Context,
    handler: MissionHandler,
) : TMap(context, handler) {

    fun drawMoveLine(points: ArrayList<TMapPoint>, id: String, color: Int) {
        val polyLine = TMapPolyLine(id, points).apply {
            lineColor = color
            outLineColor = color
        }
        tMapView.addTMapPolyLine(polyLine)
    }

    companion object {
        private var PERSON_LINE_NUM = 0
    }

}