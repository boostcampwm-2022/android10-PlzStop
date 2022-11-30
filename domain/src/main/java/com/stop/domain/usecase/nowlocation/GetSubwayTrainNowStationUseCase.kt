package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.SubwayTrainRealTimePositionUseCaseItem

interface GetSubwayTrainNowStationUseCase {

    suspend operator fun invoke(trainNumber: String, subwayNumber: Int): SubwayTrainRealTimePositionUseCaseItem

}