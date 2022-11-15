package com.stop.data.remote.source.route

import com.stop.data.remote.model.route.RouteResponse
import com.stop.domain.model.RouteRequest

interface RouteRemoteDataSource {

    fun getRoute(routeRequest: RouteRequest): RouteResponse
}