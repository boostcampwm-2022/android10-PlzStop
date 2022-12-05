package com.stop.data.remote.source.nowlocation

import com.stop.domain.model.nowlocation.BusCurrentInformation
import com.stop.data.remote.model.nowlocation.subway.TrainLocationInfo

interface NowLocationRemoteDataSource {

    suspend fun getBusNowLocation(busRouteId: String): List<BusCurrentInformation>

    suspend fun getSubwayTrainNowStation(subwayNumber: Int): List<TrainLocationInfo>
}