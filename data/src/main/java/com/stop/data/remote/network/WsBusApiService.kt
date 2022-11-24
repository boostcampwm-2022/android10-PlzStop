package com.stop.data.remote.network

import com.stop.data.BuildConfig
import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.model.nowlocation.GetBusNowLocationResponse
import com.stop.domain.model.route.seoul.bus.GetBusStationArsIdResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface WsBusApiService {

    @GET(GET_BUS_ARS_URL)
    suspend fun getBusArsId(
        @Query("stSrch") stationName: String,
        @Query("resultType") resultType: String = "json",
    ): NetworkResult<GetBusStationArsIdResponse>

    @GET(GET_BUS_NOW_LOCATION_URL)
    suspend fun getBusNowLocation(
        @Query("ServiceKey") serviceKey: String = BuildConfig.BUS_KEY,
        @Query("busRouteId") busRouteId: String,
        @Query("resultType") resultType: String = JSON
    ): NetworkResult<GetBusNowLocationResponse>

    companion object {
        private const val GET_BUS_ARS_URL = "stationinfo/getStationByName"
        private const val GET_BUS_NOW_LOCATION_URL = "buspos/getBusPosByRtid"
        private const val JSON = "json"
    }
}