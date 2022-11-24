package com.stop.ui.util

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.skt.tmap.TMapGpsManager
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import com.stop.BuildConfig
import com.stop.R
import com.stop.model.Location
import com.stop.ui.map.MapHandler
import com.stop.ui.mission.MissionHandler

open class TMap(
    private val context: Context,
    private val handler: Handler
) {
    lateinit var tMapView: TMapView
    var isTracking = true
    lateinit var initLocation: Location

    fun init() {
        tMapView = TMapView(context).apply {
            setSKTMapApiKey(BuildConfig.TMAP_APP_KEY)
            setOnMapReadyListener {
                tMapView.setVisibleLogo(false)
                tMapView.mapType = TMapView.MapType.DEFAULT
                tMapView.zoomLevel = 16

                when (this@TMap.handler) {
                    is MissionHandler -> {
                        (this@TMap.handler).alertTMapReady()
                        (this@TMap.handler).setOnEnableScrollWithZoomLevelListener()
                    }
                    is MapHandler -> {
                        (this@TMap.handler).alertTMapReady()
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
        if (location != null && checkKoreaLocation(location)) {
            val beforeLocation = tMapView.locationPoint
            val nowLocation = TMapPoint(location.latitude, location.longitude)
            when (handler) {
                is MissionHandler -> {
                    if (Location(beforeLocation.latitude, beforeLocation.longitude) != initLocation) {
                        handler.setOnLocationChangeListener(nowLocation, beforeLocation)
                    }
                }
                is MapHandler -> {
                    handler.setOnLocationChangeListener(location)
                }
            }

            tMapView.setLocationPoint(location.latitude, location.longitude)

            makeMarker(
                PERSON_MARKER,
                PERSON_MARKER_IMG,
                nowLocation
            )

            if (isTracking) {
                tMapView.setCenterPoint(location.latitude, location.longitude, true)
            }
        }
    }

    private fun checkKoreaLocation(location: android.location.Location): Boolean {
        return location.longitude > KOREA_LONGITUDE_MIN && location.longitude < KOREA_LONGITUDE_MAX
                && location.latitude > KOREA_LATITUDE_MIN && location.latitude < KOREA_LATITUDE_MAX
    }

    fun makeMarker(id: String, icon: Int, location: TMapPoint) {
        val marker = TMapMarkerItem().apply {
            this.id = id
            this.icon = ContextCompat.getDrawable(
                context,
                icon
            )?.toBitmap()
            tMapPoint = location
        }

        tMapView.removeTMapMarkerItem(id)
        tMapView.addTMapMarkerItem(marker)
    }

    companion object {
        private const val KOREA_LATITUDE_MIN = 32.814978
        private const val KOREA_LATITUDE_MAX = 39.036253

        private const val KOREA_LONGITUDE_MIN = 124.661865
        private const val KOREA_LONGITUDE_MAX = 132.550049

        private const val PERSON_MARKER = "marker_person_pin"
        private const val PERSON_MARKER_IMG = R.drawable.ic_person_pin
    }
}
