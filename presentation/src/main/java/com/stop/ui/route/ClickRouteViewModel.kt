package com.stop.ui.route

import androidx.lifecycle.ViewModel
import com.stop.domain.model.route.TransportLastTime
import com.stop.domain.model.route.tmap.custom.Itinerary

class ClickRouteViewModel : ViewModel() {

    var clickRoute : Itinerary? = null
    var lastTimes: List<TransportLastTime?>? = null
    val lastTime = "23:30:00"

}