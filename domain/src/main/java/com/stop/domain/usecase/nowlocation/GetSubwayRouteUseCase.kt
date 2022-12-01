package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.SubwayRouteLocationUseCaseItem
import com.stop.domain.model.route.tmap.RouteRequest

interface GetSubwayRouteUseCase {

    suspend operator fun invoke(
        routeRequest: RouteRequest,
        subwayLine: String,
        startSubwayStation: String,
        endSubwayStation: String
    ): SubwayRouteLocationUseCaseItem

}