package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.route.TransportLastTime
import com.stop.domain.repository.NowLocationRepository
import javax.inject.Inject

class GetBusesOnRouteUseCaseImpl @Inject constructor(
    private val nowLocationRepository: NowLocationRepository
) : GetBusesOnRouteUseCase {

    override suspend fun invoke(transportLastTime: TransportLastTime): List<String> {
        val busCurrentInformation = nowLocationRepository.getBusesOnRoute(transportLastTime.routeId)

        val stationsUntilStart = transportLastTime.stationsUntilStart.map { it.stationId }

        return busCurrentInformation.dropLastWhile {
            stationsUntilStart.contains(it.beforeNodeId).not()
        }.map { it.vehicleId }
    }
}