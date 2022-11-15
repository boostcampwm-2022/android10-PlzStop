package com.stop.domain.usecase

import com.stop.domain.model.RouteRequest
import com.stop.domain.model.RouteResponse

interface GetRouteUseCase {

    fun getRoute(routeRequest: RouteRequest): RouteResponse
}