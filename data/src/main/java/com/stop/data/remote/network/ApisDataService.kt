package com.stop.data.remote.network

import com.stop.data.remote.XmlResponse
import com.stop.data.remote.model.NetworkResult
import com.stop.domain.model.route.gyeonggi.GetGyeonggiBusStationIdResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface ApisDataService {

    @XmlResponse
    @GET
    suspend fun getBusStationId(
        @Url url: String,
    ): NetworkResult<GetGyeonggiBusStationIdResponse>
}