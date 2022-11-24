package com.stop.domain.usecase.nowlocation

import com.stop.domain.repository.NowLocationRepository
import javax.inject.Inject

class GetBusNowLocationUseCaseImpl @Inject constructor(
    private val nowLocationRepository: NowLocationRepository
) : GetBusNowLocationUseCase{

    override suspend fun getBusNowLocation(busRouteId: String) {
        nowLocationRepository.getBusNowLocation(busRouteId)
    }

}