package com.stop.domain.repository

import com.stop.domain.model.nowlocation.BusCurrentInformation
import com.stop.domain.model.nowlocation.TrainLocationInfoDomain

interface NowLocationRepository {

    suspend fun getBusesOnRoute(busRouteId: String): List<BusCurrentInformation>

    suspend fun getSubwayTrains(subwayNumber: Int): List<TrainLocationInfoDomain>
}