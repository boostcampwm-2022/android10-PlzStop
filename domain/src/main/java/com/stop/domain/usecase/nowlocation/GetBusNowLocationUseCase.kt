package com.stop.domain.usecase.nowlocation

interface GetBusNowLocationUseCase {

    suspend fun getBusNowLocation(busRouteId: String)

}