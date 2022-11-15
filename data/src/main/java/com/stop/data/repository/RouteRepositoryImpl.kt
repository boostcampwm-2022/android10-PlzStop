package com.stop.data.repository

import com.stop.data.remote.source.route.RouteRemoteDataSource
import com.stop.domain.model.RouteRequest
import com.stop.domain.model.RouteResponse
import com.stop.domain.repository.RouteRepository
import javax.inject.Inject

internal class RouteRepositoryImpl @Inject constructor(
    private val remoteDataSource: RouteRemoteDataSource
): RouteRepository {

    override fun getRoute(routeRequest: RouteRequest): RouteResponse {
        return remoteDataSource.getRoute(routeRequest).toDomain()
    }
}