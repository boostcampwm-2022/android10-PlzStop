package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.SubwayTrainRealTimePositionUseCaseItem

interface GetSubwayTrainNowLocationUseCase {

    suspend operator fun invoke(trainNumber: String, subwayNumber: Int): SubwayTrainRealTimePositionUseCaseItem

}