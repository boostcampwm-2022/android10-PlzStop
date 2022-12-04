package com.stop.domain.repository

import com.stop.domain.model.nowlocation.BusInfoUseCaseItem
import com.stop.domain.model.nowlocation.TrainLocationInfoDomain

interface NowLocationRepository {

    suspend fun getBusNowLocation(busRouteId: String, order: Int): BusInfoUseCaseItem

    suspend fun getSubwayTrains(subwayNumber: Int): List<TrainLocationInfoDomain>
}