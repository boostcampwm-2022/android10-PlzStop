package com.stop.ui.mission

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.skt.tmap.TMapPoint
import com.skt.tmap.overlay.TMapPolyLine
import com.stop.domain.model.route.tmap.custom.WalkRoute
import com.stop.ui.util.TMap

class MissionTMap(
    private val context: Context,
    handler: MissionHandler,
) : TMap(context, handler) {

    fun drawMoveLine(nowLocation: TMapPoint, beforeLocation: TMapPoint, id: String, color: Int) {
        val points = arrayListOf(nowLocation, beforeLocation)
        val polyLine = TMapPolyLine(id, points).apply {
            lineColor = ContextCompat.getColor(context, color)
            lineWidth = LINE_WIDTH
            outLineColor = Color.WHITE
            outLineWidth = OUT_LINE_WIDTH
        }
        
        tMapView.addTMapPolyLine(polyLine)
    }

    fun drawWalkLines(points: ArrayList<TMapPoint>, id: String, color: Int) {
        val polyLine = TMapPolyLine(id, points).apply {
            lineColor = ContextCompat.getColor(context, color)
            outLineColor = ContextCompat.getColor(context, color)
        }
        tMapView.addTMapPolyLine(polyLine)
    }

    fun drawWalkRoute(route: WalkRoute, linePoints: ArrayList<TMapPoint>) {
        route.steps.forEach { step ->
            step.lineString.split(" ").forEach { coordinate ->
                val points = coordinate.split(",")

                linePoints.add(TMapPoint(points.last().toDouble(), points.first().toDouble()))
            }
        }
    }

    companion object {
        private const val LINE_WIDTH = 7F
        private const val OUT_LINE_WIDTH = 10F
    }
}