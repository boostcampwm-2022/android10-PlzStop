package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.SubwayTrainRealTimePositionUseCaseItem
import com.stop.domain.repository.NowLocationRepository
import javax.inject.Inject

class GetSubwayTrainNowStationUseCaseImpl @Inject constructor(
    private val nowLocationRepository: NowLocationRepository
) : GetSubwayTrainNowStationUseCase {

    override suspend operator fun invoke(trainNumber: String, subwayNumber: Int): SubwayTrainRealTimePositionUseCaseItem {
        return nowLocationRepository.getSubwayTrainNowStation(trainNumber, subwayNumber)
    }

}