package com.stop.data.remote.network

import com.stop.data.remote.model.NetworkResult
import com.stop.domain.model.route.seoul.subway.SubwayStationResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface OpenApiSeoulService {

    @GET
    suspend fun getStationInfo(
        @Url url: String,
    ): NetworkResult<SubwayStationResponse>
}