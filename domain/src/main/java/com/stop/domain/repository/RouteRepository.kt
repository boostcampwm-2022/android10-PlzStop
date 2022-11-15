package com.stop.domain.repository

import com.stop.domain.model.RouteRequest
import com.stop.domain.model.RouteResponse

interface RouteRepository {

    fun getRoute(routeRequest: RouteRequest): RouteResponse
}