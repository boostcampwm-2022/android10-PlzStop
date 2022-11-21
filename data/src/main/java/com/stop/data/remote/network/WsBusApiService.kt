package com.stop.data.remote.network

import com.stop.data.remote.JsonResponse
import com.stop.data.remote.model.NetworkResult
import com.stop.domain.model.route.seoul.bus.GetBusStationArsIdResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface WsBusApiService {

    @GET
    suspend fun getBusArsId(
        @Url url: String,
    ): NetworkResult<GetBusStationArsIdResponse>
}