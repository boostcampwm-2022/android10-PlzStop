package com.stop.ui.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stop.domain.model.route.TransportLastTime
import com.stop.domain.model.route.tmap.custom.Itinerary
import com.stop.model.route.Place

class RouteResultViewModel : ViewModel() {

    private val _itinerary = MutableLiveData<Itinerary>()
    val itinerary: LiveData<Itinerary>
        get() = _itinerary

    private val _lastTimes = MutableLiveData<List<TransportLastTime?>>()
    val lastTimes: LiveData<List<TransportLastTime?>>
        get() = _lastTimes

    private val _origin = MutableLiveData<Place>()
    val origin: LiveData<Place>
        get() = _origin

    private val _destination = MutableLiveData<Place>()
    val destination: LiveData<Place>
        get() = _destination

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
}