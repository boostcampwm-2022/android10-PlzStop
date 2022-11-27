package com.stop.data.remote.network

import com.stop.data.BuildConfig
import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.model.route.gyeonggi.GetGyeonggiBusLineIdResponse
import com.stop.data.remote.model.route.gyeonggi.GetGyeonggiBusStationIdResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface ApisDataService {

    @GET(GET_GYEONGGI_BUS_STATION_ID_URL)
    suspend fun getBusStationId(
        @Query("keyword") stationName: String,
        @Query("serviceKey") key: String = BuildConfig.BUS_KEY
    ): NetworkResult<GetGyeonggiBusStationIdResponse>

    @GET(GET_GYEONGGI_BUS_LINE_ID_URL)
    suspend fun getBusLineId(
        @Query("stationId") stationId: String,
        @Query("serviceKey") key: String = BuildConfig.BUS_KEY
    ): NetworkResult<GetGyeonggiBusLineIdResponse>


    companion object {
        private const val GET_GYEONGGI_BUS_STATION_ID_URL = "busstationservice/getBusStationList"
        private const val GET_GYEONGGI_BUS_LINE_ID_URL = "busstationservice/getBusStationViaRouteList"

    }
}