package com.stop.data.remote.source.nowlocation

import com.squareup.moshi.JsonDataException
import com.stop.data.remote.model.NetworkResult
import com.stop.domain.model.nowlocation.BusCurrentInformation
import com.stop.data.remote.model.nowlocation.subway.TrainLocationInfo
import com.stop.data.remote.network.SwOpenApiSeoulService
import com.stop.data.remote.network.WsBusApiService
import javax.inject.Inject

internal class NowLocationRemoteDataSourceImpl @Inject constructor(
    private val wsBusApiService: WsBusApiService,
    private val swOpenApiSeoulService: SwOpenApiSeoulService
) : NowLocationRemoteDataSource {

    override suspend fun getBusNowLocation(busRouteId: String): List<BusCurrentInformation> {
        with(wsBusApiService.getBusNowLocation(busRouteId = busRouteId)) {
            return when (this) {
                is NetworkResult.Success -> data.busBody.busCurrentInformation
                    ?: throw JsonDataException(NO_RESULT)
                is NetworkResult.Failure -> throw IllegalArgumentException(message)
                is NetworkResult.NetworkError -> throw exception
                is NetworkResult.Unexpected -> throw  exception
            }
        }
    }

    override suspend fun getSubwayTrainNowStation(subwayNumber: Int): List<TrainLocationInfo> {
        with(swOpenApiSeoulService.getSubwayTrainNowStationInfo(stationName = subwayNumber.toString() + LINE)) {
            return when (this) {
                is NetworkResult.Success -> data.realtimePositions
                    ?: throw JsonDataException(NO_RESULT)
                is NetworkResult.Failure -> throw IllegalArgumentException(message)
                is NetworkResult.NetworkError -> throw exception
                is NetworkResult.Unexpected -> throw  exception
            }
        }
    }

    companion object {
        private const val LINE = "호선"
        private const val NO_RESULT = "검색 결과가 없습니다."
    }

}