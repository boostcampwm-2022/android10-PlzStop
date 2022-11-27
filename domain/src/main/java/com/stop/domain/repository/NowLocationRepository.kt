package com.stop.domain.repository

import com.stop.domain.model.nowlocation.BusInfoUseCaseItem

interface NowLocationRepository {

    suspend fun getBusNowLocation(busRouteId: String, order: Int): BusInfoUseCaseItem

}