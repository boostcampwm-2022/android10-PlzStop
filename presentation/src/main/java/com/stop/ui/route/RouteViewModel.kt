package com.stop.ui.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.model.route.tmap.custom.Itinerary
import com.stop.domain.usecase.GetRouteUseCase
import com.stop.model.route.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteViewModel @Inject constructor(
    private val getRouteUseCase: GetRouteUseCase,
): ViewModel() {

    private val _origin = MutableLiveData<Place>()
    val origin = MutableLiveData<Place>()

    private val _destination = MutableLiveData<Place>()
    val destination: LiveData<Place>
        get() = _destination

    private val _routeResponse = MutableLiveData<List<Itinerary>>()
    val routeResponse: LiveData<List<Itinerary>>
        get() = _routeResponse

    fun getRoute() {
        viewModelScope.launch {
            val originValue = _origin.value ?: return@launch
            val destinationValue = _destination.value ?: return@launch

            val routeRequest = RouteRequest(
                startX = originValue.coordinate.latitude,
                startY = originValue.coordinate.longitude,
                endX = destinationValue.coordinate.latitude,
                endY = destinationValue.coordinate.longitude,
            )

            _routeResponse.value = getRouteUseCase.getRoute(routeRequest)
        }
    }

    fun setOrigin(place: Place) {
        _origin.value = place
    }

    fun setDestination(place: Place) {
        _destination.value = place
    }
}