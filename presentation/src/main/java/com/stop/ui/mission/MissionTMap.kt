package com.stop.ui.mission

import android.content.Context
import com.skt.tmap.TMapPoint
import com.skt.tmap.overlay.TMapPolyLine
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

}