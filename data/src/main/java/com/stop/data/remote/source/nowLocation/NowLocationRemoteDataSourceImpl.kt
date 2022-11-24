package com.stop.data.remote.source.nowLocation

import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.network.WsBusApiService
import com.stop.domain.model.route.seoul.bus.GetBusStationArsIdResponse
import javax.inject.Inject

internal class NowLocationRemoteDataSourceImpl @Inject constructor(
    private val wsBusApiService: WsBusApiService
) : NowLocationRemoteDataSource {

    override suspend fun getBusNowLocation(busRouteId: String): GetBusStationArsIdResponse {
        with(wsBusApiService.getBusNowLocation(busRouteId = busRouteId)) {
            return when (this) {
                is NetworkResult.Success -> this.data
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw  this.exception
            }
        }
    }

}