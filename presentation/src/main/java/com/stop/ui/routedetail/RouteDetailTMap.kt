package com.stop.ui.routedetail

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.skt.tmap.TMapPoint
import com.skt.tmap.overlay.TMapPolyLine
import com.stop.R
import com.stop.domain.model.route.tmap.custom.*
import com.stop.ui.util.Marker
import com.stop.ui.util.TMap

class RouteDetailTMap(
    private val context: Context,
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
                is TransportRoute -> drawTransportRoute(route)
                is WalkRoute -> drawWalkRoute(route)
            }

            addTMapPoints(convertToTMapPoint(route.end.coordinate))
            polyLine = setPolyLine(route)
            tMapView.addTMapPolyLine(polyLine)
        }
        addStartAndDestinationMarker(routes)
        setRouteDetailFocus()
    }

    private fun drawTransportRoute(route: TransportRoute) {
        route.lines.forEach { line ->
            addTMapPoints(convertToTMapPoint(Coordinate(line.longitude, line.latitude)))
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

    private fun setPolyLine(route: Route): TMapPolyLine {
        val lineColor = when (route) {
            is TransportRoute -> Color.parseColor("#${route.routeColor}")
            is WalkRoute -> ContextCompat.getColor(context, R.color.main_yellow)
            else -> ContextCompat.getColor(context, R.color.main_light_grey)
        }

        return TMapPolyLine(route.start.name, tMapPoints).apply {
            this.lineColor = lineColor
            lineWidth = LINE_WIDTH
            outLineColor = Color.WHITE
            outLineWidth = OUT_LINE_WIDTH
        }
    }

    private fun addStartAndDestinationMarker(routes: List<Route>) {
        addMarker(
            Marker.START_MARKER,
            Marker.START_MARKER_IMG,
            convertToTMapPoint(routes.first().start.coordinate)
        )
        addMarker(
            Marker.DESTINATION_MARKER,
            Marker.DESTINATION_MARKER_IMG,
            convertToTMapPoint(routes.last().end.coordinate)
        )
    }

    private fun setRouteDetailFocus() {
        val maxLatitude = latitudes.max()
        val minLatitude = latitudes.min()
        val maxLongitude = longitudes.max()
        val minLongitude = longitudes.min()

        tMapView.setCenterPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2)
        tMapView.zoomToSpan(maxLatitude - minLatitude, maxLongitude - minLongitude)
        tMapView.zoomLevel -= 1
    }

    private fun addTMapPoints(point: TMapPoint) {
        tMapPoints.add(point)
        latitudes.add(point.latitude)
        longitudes.add(point.longitude)
    }

    private fun convertToTMapPoint(coordinate: Coordinate): TMapPoint {
        return TMapPoint(coordinate.latitude.toDouble(), coordinate.longitude.toDouble())
    }

    companion object {
        private const val LINE_WIDTH = 7F
        private const val OUT_LINE_WIDTH = 10F
    }
}