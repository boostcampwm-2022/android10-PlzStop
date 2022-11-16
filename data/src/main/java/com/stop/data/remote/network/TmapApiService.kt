package com.stop.data.remote.network

import com.stop.data.remote.model.route.RouteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TmapApiService {

    @POST(TRANSPORT_URL)
    suspend fun getRoutes(
        @Body routeRequest: Map<String, String>
    ): Response<RouteResponse>

    companion object {
        private const val TRANSPORT_URL = "transit/routes"
    }
}