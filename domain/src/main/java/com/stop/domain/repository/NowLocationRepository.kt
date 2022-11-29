package com.stop.domain.repository

import com.stop.domain.model.nowlocation.BusInfoUseCaseItem
import com.stop.domain.model.nowlocation.SubwayTrainRealTimePositionUseCaseItem

interface NowLocationRepository {

    suspend fun getBusNowLocation(busRouteId: String, order: Int): BusInfoUseCaseItem

    suspend fun getSubwayTrainNowLocation(trainNumber: Int): SubwayTrainRealTimePositionUseCaseItem

}