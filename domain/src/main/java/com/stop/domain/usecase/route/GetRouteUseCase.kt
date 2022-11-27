package com.stop.domain.usecase.route

import com.stop.domain.model.route.tmap.custom.Itinerary
import com.stop.domain.model.route.tmap.RouteRequest

interface GetRouteUseCase {

    suspend fun getRoute(routeRequest: RouteRequest): List<Itinerary>
}