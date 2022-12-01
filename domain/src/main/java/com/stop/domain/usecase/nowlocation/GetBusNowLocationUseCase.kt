package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.BusInfoUseCaseItem

interface GetBusNowLocationUseCase {

    suspend operator fun invoke(busRouteId: String): BusInfoUseCaseItem

}