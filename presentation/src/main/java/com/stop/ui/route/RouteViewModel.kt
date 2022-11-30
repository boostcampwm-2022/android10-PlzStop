package com.stop.ui.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.model.route.tmap.custom.Itinerary
import com.stop.domain.usecase.route.GetLastTransportTimeUseCase
import com.stop.domain.usecase.route.GetRouteUseCase
import com.stop.model.ErrorType
import com.stop.model.Event
import com.stop.model.route.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private val _lastTimeResponse = MutableLiveData<List<String?>>()
    val lastTimeResponse: LiveData<List<String?>>
        get() = _lastTimeResponse

    private val _errorMessage = MutableLiveData<Event<ErrorType>>()
    val errorMessage: LiveData<Event<ErrorType>>
        get() = _errorMessage

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

        viewModelScope.launch(Dispatchers.IO) {
            _routeResponse.postValue(getRouteUseCase.invoke(routeRequest))
        }
    }

    fun calculateLastTransportTime(itinerary: Itinerary) {
        checkClickedItinerary(itinerary)
        viewModelScope.launch(Dispatchers.IO) {
            val lastTimeInfo = getLastTransportTimeUseCase(itinerary)

            _lastTimeResponse.postValue(lastTimeInfo)
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
            "${route.start.name}(${lastTimes[index]})"
        }.joinToString(" -> ")
    }
}