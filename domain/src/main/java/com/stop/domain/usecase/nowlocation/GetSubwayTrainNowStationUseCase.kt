package com.stop.domain.usecase.nowlocation

import com.stop.domain.model.nowlocation.TrainLocationInfoDomain
import com.stop.domain.model.route.TransportLastTime

interface GetSubwayTrainNowStationUseCase {

    suspend operator fun invoke(transportLastTime: TransportLastTime, subwayNumber: Int): TrainLocationInfoDomain

}