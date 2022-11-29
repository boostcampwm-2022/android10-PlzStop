package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.SubwayRoadLocationUseCaseItem
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.repository.RouteRepository
import javax.inject.Inject

class GetSubwayRoadUseCaseImpl @Inject constructor(
    private val routeRepository: RouteRepository
) : GetSubwayRoadUseCase {

    override suspend fun invoke(routeRequest: RouteRequest): SubwayRoadLocationUseCaseItem {
        return routeRepository.getSubwayRoute(routeRequest)
    }

}