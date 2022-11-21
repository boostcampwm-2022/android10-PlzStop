package com.stop.data.remote.network

import com.stop.data.BuildConfig
import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.model.route.gyeonggi.GetGyeonggiBusStationIdResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApisDataService {

    @GET(GET_GYEONGGI_BUS_STATION_ID_URL)
    suspend fun getBusStationId(
        @Query("keyword") stationName: String,
        @Query("serviceKey") key: String = BuildConfig.BUS_KEY
    ): NetworkResult<GetGyeonggiBusStationIdResponse>

    companion object {
        private const val GET_GYEONGGI_BUS_STATION_ID_URL = "busstationservice/getBusStationList"
    }
}