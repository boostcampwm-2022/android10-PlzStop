package com.stop.ui.route

import androidx.lifecycle.ViewModel
import com.stop.domain.model.route.TransportLastTime
import com.stop.domain.model.route.tmap.custom.Itinerary

class ClickRouteViewModel : ViewModel() {

    var clickRoute : Itinerary? = null
    var lastTime = "21:03:00"

}