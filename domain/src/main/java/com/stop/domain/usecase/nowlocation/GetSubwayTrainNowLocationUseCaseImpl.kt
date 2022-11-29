package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.SubwayTrainRealTimePositionUseCaseItem
import com.stop.domain.repository.NowLocationRepository
import javax.inject.Inject

class GetSubwayTrainNowLocationUseCaseImpl @Inject constructor(
    private val nowLocationRepository: NowLocationRepository
) : GetSubwayTrainNowLocationUseCase {

    override suspend operator fun invoke(trainNumber: String, subwayNumber: Int): SubwayTrainRealTimePositionUseCaseItem {
        return nowLocationRepository.getSubwayTrainNowLocation(trainNumber, subwayNumber)
    }

}