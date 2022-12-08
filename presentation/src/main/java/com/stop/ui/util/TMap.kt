package com.stop.ui.util

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.skt.tmap.TMapGpsManager
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import com.stop.BuildConfig
import com.stop.model.Location
import com.stop.ui.map.MapHandler
import com.stop.ui.mission.MissionHandler
import com.stop.ui.routedetail.RouteDetailHandler
import kotlin.math.round

open class TMap(
    private val context: Context,
    private val handler: Handler
) {
    lateinit var tMapView: TMapView
    lateinit var initLocation: Location

    val latitudes = arrayListOf<Double>()
    val longitudes = arrayListOf<Double>()

    var isTracking = true

    fun init() {
        tMapView = TMapView(context).apply {
            setSKTMapApiKey(BuildConfig.TMAP_APP_KEY)
            setOnMapReadyListener {
                tMapView.setVisibleLogo(false)
                tMapView.mapType = TMapView.MapType.DEFAULT
                tMapView.zoomLevel = 16

                when (this@TMap.handler) {
                    is MapHandler -> {
                        (this@TMap.handler).alertTMapReady()
                    }
                    is RouteDetailHandler -> {
                        (this@TMap.handler).alertTMapReady()
                    }
                    is MissionHandler -> {
                        (this@TMap.handler).alertTMapReady()
                        (this@TMap.handler).setOnEnableScrollWithZoomLevelListener()
                    }
                }

                initLocation = Location(tMapView.locationPoint.latitude, tMapView.locationPoint.longitude)
            }
        }
    }

    fun setTrackingMode() {
        val manager = TMapGpsManager(context).apply {
            minDistance = 2.5F
            provider = TMapGpsManager.PROVIDER_GPS
            openGps()
            provider = TMapGpsManager.PROVIDER_NETWORK
            openGps()
        }

        manager.setOnLocationChangeListener(onLocationChangeListener)
    }

    private val onLocationChangeListener = TMapGpsManager.OnLocationChangedListener { location ->
        if (location != null && checkLocationInTMapLocation(location)) {
            val nowLocation = TMapPoint(location.latitude, location.longitude)

            (handler as MapHandler).setOnLocationChangeListener(location)
            addMarker(
                Marker.PERSON_MARKER,
                Marker.PERSON_MARKER_IMG,
                nowLocation
            )
            tMapView.setLocationPoint(location.latitude, location.longitude)

            if (isTracking) {
                tMapView.setCenterPoint(location.latitude, location.longitude, true)
            }
        }
    }

    private fun checkLocationInTMapLocation(location: android.location.Location): Boolean {
        return TMapView.MIN_LON < location.longitude && location.longitude < TMapView.MAX_LON
                && TMapView.MIN_LAT < location.latitude && location.latitude < TMapView.MAX_LAT
    }

    fun addMarker(id: String, icon: Int, location: TMapPoint, isFirst: Boolean = false) {
        val marker = TMapMarkerItem().apply {
            this.id = id
            this.icon = ContextCompat.getDrawable(context, icon)?.toBitmap()
            tMapPoint = location
            isAnimation = false
        }

        if (isFirst.not()) {
            tMapView.removeTMapMarkerItem(id)
        }
        tMapView.addTMapMarkerItem(marker)
    }

    fun setRouteDetailFocus() {
        val maxLatitude = latitudes.max()
        val minLatitude = latitudes.min()
        val maxLongitude = longitudes.max()
        val minLongitude = longitudes.min()

        tMapView.setCenterPoint((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2)
        tMapView.zoomToSpan(maxLatitude - minLatitude, maxLongitude - minLongitude)
        tMapView.zoomLevel -= 1
    }

    fun getDistance(startLatitude: Double, startLongitude: Double, endLatitude: Double, endLongitude: Double): Float {
        val startPoint = android.location.Location("Start")
        val endPoint = android.location.Location("End")

        startPoint.latitude = startLatitude
        startPoint.longitude = startLongitude
        endPoint.latitude = endLatitude
        endPoint.longitude = endLongitude
        return startPoint.distanceTo(endPoint)
    }
}
