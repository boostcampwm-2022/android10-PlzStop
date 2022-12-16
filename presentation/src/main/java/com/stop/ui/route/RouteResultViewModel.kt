package com.stop.ui.route

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stop.R
import com.stop.domain.model.route.TransportLastTime
import com.stop.domain.model.route.tmap.custom.Itinerary
import com.stop.domain.model.route.tmap.custom.MoveType
import com.stop.domain.model.route.tmap.custom.Route
import com.stop.domain.model.route.tmap.custom.TransportRoute
import com.stop.model.route.*

class RouteResultViewModel : ViewModel() {

    private val _itinerary = MutableLiveData<Itinerary>()
    val itinerary: LiveData<Itinerary>
        get() = _itinerary

    private val _lastTimes = MutableLiveData<List<TransportLastTime?>>()
    val lastTimes: LiveData<List<TransportLastTime?>>
        get() = _lastTimes

    private val _isLastTimeAvailable = MutableLiveData<Boolean>()
    val isLastTimeAvailable: LiveData<Boolean>
        get() = _isLastTimeAvailable

    private val _origin = MutableLiveData<Place>()
    val origin: LiveData<Place>
        get() = _origin

    private val _destination = MutableLiveData<Place>()
    val destination: LiveData<Place>
        get() = _destination

    private var isLastTimeChecked = false
    private var routeItemColor = 0

    fun setItineraries(itinerary: Itinerary) {
        _itinerary.value = itinerary
    }

    fun setLastTimes(lastTimes: List<TransportLastTime?>) {
        _lastTimes.value = lastTimes
    }

    fun setOrigin(originPlace: Place?) {
        _origin.value = originPlace ?: return
    }

    fun setDestination(destinationPlace: Place?) {
        _destination.value = destinationPlace ?: return
    }

    fun getRouteItems(): List<RouteItem> {
        val routeItems = mutableListOf<RouteItem>()

        itinerary.value?.routes?.forEachIndexed { index, route ->
            checkLastTime(index, route)
            routeItems.add(
                RouteItem(
                    name = getRouteItemName(index, route),
                    coordinate = route.start.coordinate,
                    mode = getRouteItemMode(route),
                    distance = getRouteItemDistance(route),
                    travelTime = route.sectionTime.toInt(),
                    lastTime = lastTimes.value?.get(index)?.lastTime,
                    beforeColor = getRouteItemColor(route, false),
                    currentColor = getRouteItemColor(route, true),
                    type = RouteItemType.PATH
                )
            )
        }
        isLastTimeChecked = false
        routeItems.add(0, routeItems.first().toFirstRouteItem())
        destination.value?.let {
            routeItems.add(routeItems.last().toLastRouteItem(it.name, it.coordinate))
        }

        return routeItems.toList()
    }

    private fun checkLastTime(index: Int, route: Route) {
        if (route is TransportRoute) {
            if (isLastTimeChecked.not()) {
                _isLastTimeAvailable.value = lastTimes.value?.get(index) != null
            }

            isLastTimeChecked = true
        }
    }

    private fun getRouteItemName(index: Int, route: Route): String {
        return if (index == 0) {
            origin.value?.name ?: ""
        } else {
            route.start.name
        }
    }

    private fun getRouteItemMode(route: Route): Int {
        return when (route.mode) {
            MoveType.WALK -> R.drawable.ic_walk_white
            MoveType.BUS -> R.drawable.ic_bus_white
            MoveType.SUBWAY -> R.drawable.ic_subway_white
            MoveType.TRANSFER -> R.drawable.ic_transfer_white
            else -> R.drawable.ic_star_white
        }
    }

    private fun getRouteItemDistance(route: Route): Double {
        return if (route.mode == MoveType.TRANSFER) {
            val startPoint = android.location.Location("Start")
            val endPoint = android.location.Location("End")

            startPoint.latitude = route.start.coordinate.latitude.toDouble()
            startPoint.longitude = route.start.coordinate.longitude.toDouble()
            endPoint.latitude = route.end.coordinate.latitude.toDouble()
            endPoint.longitude = route.end.coordinate.longitude.toDouble()
            startPoint.distanceTo(endPoint).toDouble()
        } else {
            route.distance
        }
    }

    private fun getRouteItemColor(route: Route, isCurrent: Boolean): Int {
        return if (isCurrent) {
            routeItemColor = if (route is TransportRoute) {
                Color.parseColor("#${route.routeColor}")
            } else if (route.mode == MoveType.WALK) {
                Color.parseColor(MAIN_WALK_GREY)
            } else {
                Color.parseColor(MAIN_YELLOW)
            }
            routeItemColor
        } else {
            if (routeItemColor != 0) {
                routeItemColor
            } else {
                getRouteItemColor(route, true)
            }
        }
    }

    companion object {
        private const val MAIN_WALK_GREY = "#C0C5CA"
        private const val MAIN_YELLOW = "#FFC766"
    }

}