package com.stop.data.remote.network

import com.stop.data.remote.model.route.RouteResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.QueryMap

interface TmapApiService {

    @POST(TRANSPORT_URL)
    suspend fun getRoutes(
        @QueryMap routeRequest: Map<String, String>
    ): Response<RouteResponse>

    companion object {
        private const val TRANSPORT_URL = "transit/routes"
    }
}