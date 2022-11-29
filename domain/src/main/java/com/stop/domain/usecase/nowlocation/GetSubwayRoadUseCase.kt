package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.SubwayRoadLocationUseCaseItem
import com.stop.domain.model.route.tmap.RouteRequest

interface GetSubwayRoadUseCase {

    suspend operator fun invoke(routeRequest: RouteRequest): SubwayRoadLocationUseCaseItem

}