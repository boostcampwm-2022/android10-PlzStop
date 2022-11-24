package com.stop.data.remote.network

import com.stop.data.remote.model.NetworkResult
import com.stop.domain.model.route.seoul.bus.GetBusLastTimeResponse
import com.stop.domain.model.route.seoul.bus.GetBusLineResponse
import com.stop.domain.model.route.seoul.bus.GetBusStationArsIdResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface WsBusApiService {

    @GET(GET_BUS_ARS_URL)
    suspend fun getBusArsId(
        @Query("stSrch") stationName: String,
        @Query("resultType") resultType: String = "json",
    ): NetworkResult<GetBusStationArsIdResponse>

    @GET(GET_BUS_LINE_URL)
    suspend fun getBusLine(
        @Query("arsId") stationId: String,
        @Query("resultType") resultType: String = "json",
    ): NetworkResult<GetBusLineResponse>

    @GET(GET_BUS_LAST_TIME_URL)
    suspend fun getBusLastTime(
        @Query("arsId") stationId: String,
        @Query("busRouteId") lineId: String,
        @Query("resultType") resultType: String = "json",
    ): NetworkResult<GetBusLastTimeResponse>

    companion object {
        private const val GET_BUS_ARS_URL = "stationinfo/getStationByName"
        private const val GET_BUS_LINE_URL = "stationinfo/getRouteByStation"
        private const val GET_BUS_LAST_TIME_URL = "stationinfo/getBustimeByStation"
    }
}