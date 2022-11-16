package com.stop.domain.repository

import com.stop.domain.model.RouteRequest
import com.stop.domain.model.RouteResponse

interface RouteRepository {

    suspend fun getRoute(routeRequest: RouteRequest): RouteResponse
}