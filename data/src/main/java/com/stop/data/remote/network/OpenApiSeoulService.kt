package com.stop.data.remote.network

import com.stop.data.BuildConfig
import com.stop.data.remote.model.NetworkResult
import com.stop.domain.model.route.seoul.subway.SubwayLastTimeResponse
import com.stop.domain.model.route.seoul.subway.SubwayStationResponse
import com.stop.domain.model.route.seoul.subway.SubwayStationsResponse
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

    @GET("{KEY}/{TYPE}/{SERVICE}/{START_INDEX}/{END_INDEX}/%20/%20/{LINE_NAME}")
    suspend fun getSubwayStations(
        @Path("KEY") key: String = BuildConfig.SUBWAY_KEY,
        @Path("TYPE") type: String = "json",
        @Path("SERVICE") serviceName: String,
        @Path("LINE_NAME") lineName: String,
        @Path("START_INDEX") startIndex: Int = 1,
        @Path("END_INDEX") endIndex: Int = 200,
    ): NetworkResult<SubwayStationsResponse>

    @GET("{KEY}/{TYPE}/{SERVICE}/{START_INDEX}/{END_INDEX}/{STATION_CD}/{WEEK_TAG}/{INOUT_TAG}")
    suspend fun getSubwayLastTime(
        @Path("KEY") key: String = BuildConfig.SUBWAY_KEY,
        @Path("TYPE") type: String = "json",
        @Path("SERVICE") serviceName: String,
        @Path("STATION_CD") stationId: String,
        @Path("WEEK_TAG") weekTag: String,
        @Path("INOUT_TAG") inOutTag: String,
        @Path("START_INDEX") startIndex: Int = 1,
        @Path("END_INDEX") endIndex: Int = 20,
    ): NetworkResult<SubwayLastTimeResponse>
}