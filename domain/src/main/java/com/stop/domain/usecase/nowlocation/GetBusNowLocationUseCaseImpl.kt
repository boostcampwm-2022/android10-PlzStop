package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.BusInfoUseCaseItem
import com.stop.domain.repository.NowLocationRepository
import javax.inject.Inject

class GetBusNowLocationUseCaseImpl @Inject constructor(
    private val nowLocationRepository: NowLocationRepository
) : GetBusNowLocationUseCase {

    override suspend fun getBusNowLocation(busRouteId: String): BusInfoUseCaseItem {
        return nowLocationRepository.getBusNowLocation(busRouteId, TEST_ORDER)
    }

    companion object {
        private const val TEST_ORDER = 1
    }

}