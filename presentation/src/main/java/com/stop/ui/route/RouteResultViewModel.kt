package com.stop.ui.route

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.stop.domain.model.route.TransportLastTime
import com.stop.domain.model.route.tmap.custom.Itinerary

class RouteResultViewModel : ViewModel() {

    private val _itinerary = MutableLiveData<Itinerary>()
    val itinerary: LiveData<Itinerary>
        get() = _itinerary

    private val _lastTimes = MutableLiveData<List<TransportLastTime?>>()
    val lastTimes: LiveData<List<TransportLastTime?>>
        get() = _lastTimes

    fun setItineraries(itinerary: Itinerary) {
        _itinerary.value = itinerary
    }

    fun setLastTimes(lastTimes: List<TransportLastTime?>) {
        _lastTimes.value = lastTimes
    }
}