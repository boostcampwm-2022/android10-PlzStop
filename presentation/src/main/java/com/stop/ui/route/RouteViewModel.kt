package com.stop.ui.route

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stop.R
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.model.route.tmap.custom.*
import com.stop.domain.usecase.route.GetLastTransportTimeUseCase
import com.stop.domain.usecase.route.GetRouteUseCase
import com.stop.model.ErrorType
import com.stop.model.Event
import com.stop.model.route.*
import com.stop.model.route.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val getRouteUseCase: GetRouteUseCase,
    private val getLastTransportTimeUseCase: GetLastTransportTimeUseCase,
) : ViewModel() {

    var clickedItineraryIndex: Int = -1

    private val _origin = MutableLiveData<Place>()
    val origin: LiveData<Place>
        get() = _origin

    private val _destination = MutableLiveData<Place>()
    val destination: LiveData<Place>
        get() = _destination

    private val _routeResponse = MutableLiveData<List<Itinerary>>()
    val routeResponse: LiveData<List<Itinerary>>
        get() = _routeResponse

    private val _lastTimeResponse = MutableLiveData<Event<List<String?>>>()
    val lastTimeResponse: LiveData<Event<List<String?>>>
        get() = _lastTimeResponse

    private val _errorMessage = MutableLiveData<Event<ErrorType>>()
    val errorMessage: LiveData<Event<ErrorType>>
        get() = _errorMessage

    var tempItinerary: Itinerary = Itinerary("", listOf(), 0.0, 0, 0, 0)
    var tempLastTime = mutableListOf<String?>()
    var routeItemColor = 0

    fun getRoute() {
        val originValue = _origin.value ?: let {
            _errorMessage.value = Event(ErrorType.NO_START)
            return
        }

        val destinationValue = _destination.value ?: let {
            _errorMessage.value = Event(ErrorType.NO_END)
            return
        }

        val routeRequest = RouteRequest(
            startX = originValue.coordinate.longitude,
            startY = originValue.coordinate.latitude,
            endX = destinationValue.coordinate.longitude,
            endY = destinationValue.coordinate.latitude,
        )

        viewModelScope.launch {
            this@RouteViewModel._routeResponse.value = getRouteUseCase(routeRequest)
        }
    }

    fun calculateLastTransportTime(itinerary: Itinerary) {
        checkClickedItinerary(itinerary)
        viewModelScope.launch {
            this@RouteViewModel._lastTimeResponse.value = Event(getLastTransportTimeUseCase(itinerary))
        }
    }

    private fun checkClickedItinerary(itinerary: Itinerary) {
        clickedItineraryIndex = _routeResponse.value?.indexOf(itinerary) ?: -1
    }

    fun setOrigin(place: Place) {
        _origin.value = place
    }

    fun setDestination(place: Place) {
        _destination.value = place
    }

    fun getResult(): String {
        val clickedItinerary = _routeResponse.value?.get(clickedItineraryIndex) ?: return "함수를 잘못 호출했습니다."
        val lastTimes = _lastTimeResponse.value ?: return "이 함수를 호출한 시점에 막차 데이터가 null인 논리적 오류가 발생했습니다."

        return clickedItinerary.routes.mapIndexed { index, route ->
            "${route.start.name}(${lastTimes.peekContent()[index]})"
        }.joinToString(" -> ")
    }

    fun getRouteItems(): List<RouteItem> {
        val routeItems = mutableListOf<RouteItem>()

        tempItinerary.routes.forEachIndexed { index, route ->
            routeItems.add(
                RouteItem(
                    name = getRouteItemName(index, route),
                    coordinate = route.start.coordinate,
                    mode = getRouteItemMode(route),
                    distance = route.distance,
                    travelTime = route.sectionTime.toInt(),
                    lastTime = tempLastTime[index],
                    beforeColor = getRouteItemColor(route, false),
                    currentColor = getRouteItemColor(route, true),
                    type = RouteItemType.PATH
                )
            )
        }
        routeItems.add(0, routeItems.first().toFirstRouteItem())
        destination.value?.let {
            routeItems.add(routeItems.last().toLastRouteItem(it.name, it.coordinate))
        }

        return routeItems.toList()
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
            else -> R.drawable.ic_star_white
        }
    }

    private fun getRouteItemColor(route: Route, isCurrent: Boolean): Int {
        return if (isCurrent) {
            routeItemColor = when (route) {
                is TransportRoute -> Color.parseColor("#${route.routeColor}")
                is WalkRoute -> Color.parseColor(MAIN_WALK_GREY)
                else -> Color.parseColor(MAIN_YELLOW)
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