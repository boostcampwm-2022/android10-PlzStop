package com.stop.data.remote.network

import com.stop.data.remote.model.NetworkResult
import com.stop.domain.model.route.seoul.bus.GetBusStationArsIdResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WsBusApiService {

    @GET(GET_BUS_ARS_URL)
    suspend fun getBusArsId(
        @Query("stSrch") stationName: String,
        @Query("resultType") resultType: String = "json",
    ): NetworkResult<GetBusStationArsIdResponse>

    companion object {
        private const val GET_BUS_ARS_URL = "stationinfo/getStationByName"
    }
}