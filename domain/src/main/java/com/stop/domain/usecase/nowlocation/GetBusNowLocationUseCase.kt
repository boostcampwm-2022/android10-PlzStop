package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.BusCurrentInformationUseCaseItem
import com.stop.domain.model.route.TransportLastTime

interface GetBusNowLocationUseCase {

    suspend operator fun invoke(
        transportLastTime: TransportLastTime,
        busVehicleIds: List<String>,
    ): List<BusCurrentInformationUseCaseItem>

}