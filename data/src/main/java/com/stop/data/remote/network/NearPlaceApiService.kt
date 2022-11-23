package com.stop.data.remote.network

import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.model.nearplace.NearPlaceResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface NearPlaceApiService {

    @GET("/tmap/pois")
    suspend fun getNearPlaces(
        @Query("version") version: Int,
        @Query("searchKeyword") searchKeyword: String,
        @Query("centerLon") centerLon: Double,
        @Query("centerLat") centerLat: Double,
        @Query("appKey") appKey: String,
    ): NetworkResult<NearPlaceResponse>
}