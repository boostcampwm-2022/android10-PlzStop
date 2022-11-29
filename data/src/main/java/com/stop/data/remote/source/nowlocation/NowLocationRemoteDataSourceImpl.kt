package com.stop.data.remote.source.nowlocation

import com.stop.data.model.nowlocation.BusInfoRepositoryItem
import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.network.WsBusApiService
import javax.inject.Inject

internal class NowLocationRemoteDataSourceImpl @Inject constructor(
    private val wsBusApiService: WsBusApiService
) : NowLocationRemoteDataSource {

    override suspend fun getBusNowLocation(busRouteId: String, order: Int): BusInfoRepositoryItem {
        with(wsBusApiService.getBusNowLocation(busRouteId = busRouteId)) {
            return when (this) {
                is NetworkResult.Success -> this.data.busBody.busInfo[order].toRepositoryModel()
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw  this.exception
            }
        }
    }

    override suspend fun getSubwayTrainNowLocation(trainNumber: Int): Any {
        TODO("Not yet implemented")
    }

}