package com.stop.data.remote.source.nowlocation

import com.stop.data.model.nowlocation.BusInfoRepositoryItem
import com.stop.data.remote.model.nowlocation.subway.TrainLocationInfo

interface NowLocationRemoteDataSource {

    suspend fun getBusNowLocation(busRouteId: String, order: Int): BusInfoRepositoryItem

    suspend fun getSubwayTrainNowStation(subwayNumber: Int): List<TrainLocationInfo>
}