package com.stop.domain.usecase

import com.stop.domain.model.RouteRequest
import com.stop.domain.model.RouteResponse
import com.stop.domain.repository.RouteRepository
import javax.inject.Inject

internal class GetRouteUseCaseImpl @Inject constructor(
    private val routeRepository: RouteRepository
): GetRouteUseCase {

    override suspend fun getRoute(routeRequest: RouteRequest): RouteResponse {
        return routeRepository.getRoute(routeRequest)
    }
}