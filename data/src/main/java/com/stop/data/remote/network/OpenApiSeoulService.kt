package com.stop.data.remote.network

import com.stop.data.BuildConfig
import com.stop.data.remote.model.NetworkResult
import com.stop.domain.model.route.seoul.subway.SubwayStationResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface OpenApiSeoulService {

    @GET("{KEY}/{TYPE}/{SERVICE}/{START_INDEX}/{END_INDEX}/{STATION_NM}/")
    suspend fun getStationInfo(
        @Path("KEY") key: String = BuildConfig.SUBWAY_KEY,
        @Path("SERVICE") serviceName: String,
        @Path("TYPE") type: String = "json",
        @Path("STATION_NM") stationName: String,
        @Path("START_INDEX") startIndex: Int = 1,
        @Path("END_INDEX") endIndex: Int = 5,
    ): NetworkResult<SubwayStationResponse>
}