package com.stop.data.remote.source.route

import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.model.route.RouteResponse
import com.stop.data.remote.network.FakeTmapApiService
import com.stop.domain.model.RouteRequest
import javax.inject.Inject

internal class RouteRemoteDataSourceImpl @Inject constructor(
//    private val tmapApiService: TmapApiService,
    private val fakeTmapApiService: FakeTmapApiService,
): RouteRemoteDataSource {
    override suspend fun getRoute(routeRequest: RouteRequest): RouteResponse {
        with(fakeTmapApiService.getRoutes(routeRequest.toMap())) {
            return when(this) {
                is NetworkResult.Success -> this.data
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    companion object {
        private const val BODY_IS_EMPTY = "response body가 비어 있습니다."
    }
}