package com.stop.remote.network

import com.stop.remote.model.NetworkResult
import com.stop.remote.model.nearplace.NearPlcaeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NearPlaceApiService {

    @GET("/tmap/pois")
    suspend fun getNearPlaceList(
        @Query("version") version: Int,
        @Query("searchKeyword") searchKeyword: String,
        @Query("centerLon") centerLon: Float,
        @Query("centerLat") centerLat: Float,
        @Query("appKey") appKey: String,
    ): NetworkResult<NearPlcaeResponse>

}