package com.stop.remote.source.nearplace

import com.stop.remote.model.NetworkResult
import com.stop.remote.model.nearplace.NearPlcaeResponse

interface NearPlaceRemoteDataSource {

    suspend fun getNearPlaceList(
        version : Int,
        searchKeyword: String,
        centerLon: Float,
        centerLat: Float,
        appKey: String
    ): NetworkResult<NearPlcaeResponse>

}