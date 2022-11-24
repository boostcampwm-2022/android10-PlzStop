package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.BusInfoUseCaseItem

interface GetBusNowLocationUseCase {

    suspend fun getBusNowLocation(busRouteId: String): BusInfoUseCaseItem

}