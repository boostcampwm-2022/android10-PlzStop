package com.stop.remote.source.nearplace

import com.stop.remote.model.NetworkResult
import com.stop.remote.model.nearplace.NearPlcaeResponse
import com.stop.remote.network.NearPlaceApiService
import javax.inject.Inject

class NearPlaceRemoteDataSourceImpl @Inject constructor(
    private val nearPlaceApiService: NearPlaceApiService
) : NearPlaceRemoteDataSource {

    override suspend fun getNearPlaceList(
        version: Int,
        searchKeyword: String,
        centerLon: Float,
        centerLat: Float,
        appKey: String
    ): NetworkResult<NearPlcaeResponse> {
        return nearPlaceApiService.getNearPlaceList(
            version,
            searchKeyword,
            centerLon,
            centerLat,
            appKey
        )
    }
}