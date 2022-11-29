package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.SubwayRouteLocationUseCaseItem
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.repository.RouteRepository
import javax.inject.Inject

class GetSubwayRouteUseCaseImpl @Inject constructor(
    private val routeRepository: RouteRepository
) : GetSubwayRouteUseCase {

    override suspend fun invoke(routeRequest: RouteRequest): SubwayRouteLocationUseCaseItem {
        return routeRepository.getSubwayRoute(routeRequest)
    }

}