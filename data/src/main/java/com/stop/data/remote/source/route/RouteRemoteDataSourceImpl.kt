package com.stop.data.remote.source.route

import com.stop.data.remote.model.route.RouteResponse
import com.stop.data.remote.network.TmapApiService
import com.stop.domain.model.RouteRequest
import javax.inject.Inject

internal class RouteRemoteDataSourceImpl @Inject constructor(
    private val tmapApiService: TmapApiService,
): RouteRemoteDataSource {
    override fun getRoute(routeRequest: RouteRequest): RouteResponse {
        val response = tmapApiService.getRoutes(routeRequest.toMap())

        return when(response.isSuccessful) {
            true -> response.body() ?: throw IllegalArgumentException(BODY_IS_EMPTY)
            false -> throw IllegalArgumentException(response.message())
        }
    }

    companion object {
        private const val BODY_IS_EMPTY = "response body가 비어 있습니다."
    }
}