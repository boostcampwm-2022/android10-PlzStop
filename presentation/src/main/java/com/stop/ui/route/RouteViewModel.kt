package com.stop.ui.route

import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stop.domain.model.route.TransportLastTime
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.model.route.tmap.custom.*
import com.stop.domain.usecase.route.GetLastTransportTimeUseCase
import com.stop.domain.usecase.route.GetRouteUseCase
import com.stop.model.ErrorType
import com.stop.model.Event
import com.stop.model.route.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val getRouteUseCase: GetRouteUseCase,
    private val getLastTransportTimeUseCase: GetLastTransportTimeUseCase,
) : ViewModel() {

    private var clickedItineraryIndex: Int = -1
    var alertDialog: AlertDialog? = null

    private val _origin = MutableLiveData<Place>()
    val origin: LiveData<Place>
        get() = _origin

    private val _destination = MutableLiveData<Place>()
    val destination: LiveData<Place>
        get() = _destination

    private val _routeResponse = MutableLiveData<List<Itinerary>>()
    val routeResponse: LiveData<List<Itinerary>>
        get() = _routeResponse

    private val _lastTimeResponse = MutableLiveData<Event<List<TransportLastTime?>>>()
    val lastTimeResponse: LiveData<Event<List<TransportLastTime?>>>
        get() = _lastTimeResponse

    private val _errorMessage = MutableLiveData<Event<ErrorType>>()
    val errorMessage: LiveData<Event<ErrorType>>
        get() = _errorMessage

    private val _isLoading = MutableLiveData<Event<Boolean>>()
    val isLoading: LiveData<Event<Boolean>>
        get() = _isLoading

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        val errorMessage = when (throwable) {
            is SocketTimeoutException -> Event(ErrorType.SOCKET_TIMEOUT_EXCEPTION)
            is UnknownHostException -> Event(ErrorType.UNKNOWN_HOST_EXCEPTION)
            else -> Event(ErrorType.UNKNOWN_EXCEPTION)
        }
        _errorMessage.postValue(errorMessage)
        _isLoading.postValue(Event(false))
    }

    fun patchRoute(isShowError: Boolean = true) {
        val originValue = _origin.value ?: let {
            if (!isShowError) {
                return
            }
            _errorMessage.value = Event(ErrorType.NO_START)
            return
        }

        val destinationValue = _destination.value ?: let {
            if (!isShowError) {
                return
            }
            _errorMessage.value = Event(ErrorType.NO_END)
            return
        }
        _isLoading.value = Event(true)

        val routeRequest = RouteRequest(
            startX = originValue.coordinate.longitude,
            startY = originValue.coordinate.latitude,
            endX = destinationValue.coordinate.longitude,
            endY = destinationValue.coordinate.latitude,
        )

        viewModelScope.launch(Dispatchers.Default + coroutineExceptionHandler) {
            val itineraries = getRouteUseCase(routeRequest)
            if (itineraries.isEmpty()) {
                _errorMessage.postValue(Event(ErrorType.NO_ROUTE_RESULT))
                _routeResponse.postValue(listOf())
                _isLoading.postValue(Event(false))
                return@launch
            }
            this@RouteViewModel._routeResponse.postValue(itineraries)
            _isLoading.postValue(Event(false))
        }
    }

    fun changeOriginAndDestination() {
        _origin.value = _destination.value.also {
            _destination.value = _origin.value
        }
        patchRoute(false)
    }

    fun calculateLastTransportTime(itinerary: Itinerary) {
        checkClickedItinerary(itinerary)
        viewModelScope.launch(Dispatchers.Default + coroutineExceptionHandler) {
            this@RouteViewModel._lastTimeResponse.postValue(Event(getLastTransportTimeUseCase(itinerary)))
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
}