package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.BusCurrentInformationUseCaseItem
import com.stop.domain.model.nowlocation.TransportState
import com.stop.domain.model.route.TransportLastTime
import com.stop.domain.repository.NowLocationRepository
import javax.inject.Inject

class GetBusNowLocationUseCaseImpl @Inject constructor(
    private val nowLocationRepository: NowLocationRepository
) : GetBusNowLocationUseCase {

    override suspend operator fun invoke(
        transportLastTime: TransportLastTime,
        busVehicleIds: List<String>
    ): List<BusCurrentInformationUseCaseItem> {
        val busCurrentInformation = nowLocationRepository.getBusesOnRoute(transportLastTime.routeId)

        val stationsUntilStart = transportLastTime.stationsUntilStart.map { it.stationId }

        return busVehicleIds.map { busVehicleId ->
            val newCurrentInformation = busCurrentInformation.firstOrNull {
                it.vehicleId == busVehicleId
            } ?: return@map BusCurrentInformationUseCaseItem.createDisappearItem()

            if (stationsUntilStart.contains(newCurrentInformation.beforeNodeId)) {
                return@map newCurrentInformation.toUseCaseModel(TransportState.RUN)
            }
            return@map newCurrentInformation.toUseCaseModel(TransportState.ARRIVE)
        }
    }
}