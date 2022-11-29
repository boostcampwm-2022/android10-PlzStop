package com.stop.data.remote.network

import com.stop.data.BuildConfig
import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.model.route.gyeonggi.GyeonggiBusLastTimeResponse
import com.stop.data.remote.model.route.gyeonggi.GyeonggiBusLineIdResponse
import com.stop.data.remote.model.route.gyeonggi.GyeonggiBusRouteStationsResponse
import com.stop.data.remote.model.route.gyeonggi.GyeonggiBusStationIdResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface ApisDataService {

    @GET(GET_GYEONGGI_BUS_STATION_ID_URL)
    suspend fun getBusStationId(
        @Query("keyword") stationName: String,
        @Query("serviceKey") key: String = BuildConfig.BUS_KEY
    ): NetworkResult<GyeonggiBusStationIdResponse>

    @GET(GET_GYEONGGI_BUS_LINE_ID_URL)
    suspend fun getBusLineId(
        @Query("stationId") stationId: String,
        @Query("serviceKey") key: String = BuildConfig.BUS_KEY
    ): NetworkResult<GyeonggiBusLineIdResponse>

    @GET(GET_GYEONGGI_BUS_LAST_TIME_URL)
    suspend fun getBusLastTime(
        @Query("routeId") lineId: String,
        @Query("serviceKey") key: String = BuildConfig.BUS_KEY
    ): NetworkResult<GyeonggiBusLastTimeResponse>

    @GET(GET_GYEONGGI_BUS_ROUTE_STATION_URL)
    suspend fun getBusRouteStations(
        @Query("routeId") lineId: String,
        @Query("serviceKey") key: String = BuildConfig.BUS_KEY
    ): NetworkResult<GyeonggiBusRouteStationsResponse>

    companion object {
        private const val GET_GYEONGGI_BUS_STATION_ID_URL = "busstationservice/getBusStationList"
        private const val GET_GYEONGGI_BUS_LINE_ID_URL = "busstationservice/getBusStationViaRouteList"
        private const val GET_GYEONGGI_BUS_LAST_TIME_URL = "busrouteservice/getBusRouteInfoItem"
        private const val GET_GYEONGGI_BUS_ROUTE_STATION_URL = "busrouteservice/getBusRouteStationList"
    }
}