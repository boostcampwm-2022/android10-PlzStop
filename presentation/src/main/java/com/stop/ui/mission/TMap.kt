package com.stop.ui.mission

import android.content.Context
import android.location.Location
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.skt.tmap.TMapGpsManager
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import com.skt.tmap.overlay.TMapMarkerItem
import com.skt.tmap.overlay.TMapPolyLine
import com.stop.BuildConfig
import com.stop.R

class TMap(
    private val context: Context,
    private val tMapHandler: TMapHandler,
) {

    private lateinit var tMapView: TMapView
    private var isTracking = true
    private var lineNum = 0
    private val mockLocation = Location("")

    private val onLocationChangeListener = TMapGpsManager.OnLocationChangedListener { location ->
        if (location != null && checkKoreaLocation(location)) {
            val beforeLocationPoint = tMapView.locationPoint
            drawMoveLine(location, beforeLocationPoint)
            movePin(location)
        }
    }

    private fun checkKoreaLocation(location: Location): Boolean {
        return location.longitude > KOREA_LONGITUDE_MIN && location.longitude < KOREA_LONGITUDE_MAX
                && location.latitude > KOREA_LATITUDE_MIN && location.latitude < KOREA_LATITUDE_MAX
    }

    fun init() {
        tMapView = TMapView(context)
        tMapView.setSKTMapApiKey(BuildConfig.TMAP_APP_KEY)
        tMapView.setOnMapReadyListener {
            tMapView.setVisibleLogo(false)
            tMapView.mapType = TMapView.MapType.NIGHT
            tMapView.zoomLevel = 16

            tMapHandler.alertTMapReady()
        }
        tMapView.setOnEnableScrollWithZoomLevelListener { _, _ ->
            isTracking = false
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

    fun getTMapView(): TMapView {
        return tMapView
    }

    private fun drawMoveLine(location: Location, beforeLocationPoint: TMapPoint) {
        val points = arrayListOf(
            beforeLocationPoint,
            TMapPoint(location.latitude, location.longitude)
        )
        val polyLine = TMapPolyLine("line$lineNum", points)
        lineNum += 1
        tMapView.addTMapPolyLine(polyLine)
    }

    private fun movePin(location: Location) {
        val marker = TMapMarkerItem().apply {
            id = "marker_person_pin"
            icon = ContextCompat.getDrawable(context, R.drawable.ic_person_pin)?.toBitmap()
            setTMapPoint(location.latitude, location.longitude)
        }

        tMapView.removeTMapMarkerItem("marker_person_pin")
        tMapView.addTMapMarkerItem(marker)
//        tMapView.setLocationPoint(location.latitude, location.longitude)

        if (isTracking) {
            tMapView.setCenterPoint(location.latitude, location.longitude, true)
        }
    }

    fun moveLocation(longitude: String, latitude: String) {
        mockLocation.latitude = latitude.toDouble()
        mockLocation.longitude = longitude.toDouble()

        val beforeLocationPoint = tMapView.locationPoint
        drawMoveLine(mockLocation, beforeLocationPoint)
        movePin(mockLocation)
        tMapView.setLocationPoint(latitude.toDouble(), longitude.toDouble())
    }

    companion object {
        private const val KOREA_LATITUDE_MIN = 32.856
        private const val KOREA_LATITUDE_MAX = 38.561

        private const val KOREA_LONGITUDE_MAX = 130.675
        private const val KOREA_LONGITUDE_MIN = 124.564
    }
}