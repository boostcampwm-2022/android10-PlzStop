package com.stop.data.remote.network

import com.stop.data.BuildConfig
import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.model.nowlocation.subway.SubwayTrainNowLocationResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface SwOpenApiSeoulService {

    @GET("{KEY}/{TYPE}/{SERVICE}/{START_INDEX}/{END_INDEX}/{STATION_NM}/")
    suspend fun getSubwayTrainNowStationInfo(
        @Path("KEY") key: String = BuildConfig.SUBWAY_KEY,
        @Path("SERVICE") serviceName: String = REAL_TIME_POSITION,
        @Path("TYPE") type: String = JSON,
        @Path("STATION_NM") stationName: String,
        @Path("START_INDEX") startIndex: Int = START_INDEX,
        @Path("END_INDEX") endIndex: Int = END_INDEX,
    ): NetworkResult<SubwayTrainNowLocationResponse>

    companion object {
        private const val JSON = "json"
        private const val REAL_TIME_POSITION = "realtimePosition"
        private const val START_INDEX= 1
        private const val END_INDEX = 100
    }

}