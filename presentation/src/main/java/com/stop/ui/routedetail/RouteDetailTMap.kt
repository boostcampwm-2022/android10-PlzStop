package com.stop.ui.routedetail

import android.content.Context
import android.graphics.Color
import com.skt.tmap.TMapPoint
import com.skt.tmap.overlay.TMapPolyLine
import com.stop.domain.model.route.tmap.custom.*
import com.stop.ui.util.TMap

class RouteDetailTMap(
    context: Context,
    private val handler: RouteDetailHandler,
) : TMap(context, handler) {
    private val tMapPoints = arrayListOf<TMapPoint>()
    private val latitudes = arrayListOf<Double>()
    private val longitudes = arrayListOf<Double>()

    private var polyLine = TMapPolyLine()

    fun drawRoutes(routes: List<Route>) {
        routes.forEach { route ->
            tMapPoints.clear()
            addTMapPoints(convertToTMapPoint(route.start.coordinate))

            when (route) {
                is WalkRoute -> drawWalkRoute(route)
                is TransportRoute -> drawTransportRoute(route)
            }

            addTMapPoints(convertToTMapPoint(route.end.coordinate))
            polyLine = TMapPolyLine(route.start.name, tMapPoints).apply {
                lineColor = setLineColor(route)
                lineWidth = 5F
            }
            tMapView.addTMapPolyLine(polyLine)
        }
    }

    private fun drawWalkRoute(route: WalkRoute) {
        route.steps.forEach { step ->
            step.lineString.split(" ").forEach { coordinate ->
                val points = coordinate.split(",")

                addTMapPoints(TMapPoint(points.last().toDouble(), points.first().toDouble()))
            }
        }
    }

    private fun drawTransportRoute(route: TransportRoute) {
        route.lines.forEach { line ->
            addTMapPoints(convertToTMapPoint(Coordinate(line.longitude, line.latitude)))
        }
    }

    private fun addTMapPoints(point: TMapPoint) {
        tMapPoints.add(point)
        latitudes.add(point.latitude)
        longitudes.add(point.longitude)
    }

    private fun convertToTMapPoint(coordinate: Coordinate): TMapPoint {
        return TMapPoint(coordinate.latitude.toDouble(), coordinate.longitude.toDouble())
    }

    private fun setLineColor(route: Route): Int {
        return when (route.mode) {
            MoveType.WALK -> Color.GRAY
            MoveType.BUS -> Color.CYAN
            MoveType.SUBWAY -> Color.MAGENTA
            else -> Color.YELLOW
        }
    }
}