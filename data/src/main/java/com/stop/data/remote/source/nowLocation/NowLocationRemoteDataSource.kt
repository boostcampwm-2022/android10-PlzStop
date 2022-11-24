package com.stop.data.remote.source.nowLocation

import com.stop.domain.model.route.seoul.bus.GetBusStationArsIdResponse

interface NowLocationRemoteDataSource {

    suspend fun getBusNowLocation(busRouteId: String): GetBusStationArsIdResponse

}