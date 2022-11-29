package com.stop.data.remote.source.nowlocation

import com.stop.data.model.nowlocation.BusInfoRepositoryItem
import com.stop.data.model.nowlocation.SubwayTrainRealTimePositionRepositoryItem

interface NowLocationRemoteDataSource {

    suspend fun getBusNowLocation(busRouteId: String, order: Int): BusInfoRepositoryItem

    suspend fun getSubwayTrainNowLocation(trainNumber: Int): SubwayTrainRealTimePositionRepositoryItem
}