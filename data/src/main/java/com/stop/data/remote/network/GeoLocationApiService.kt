package com.stop.data.remote.network

import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.model.geoLocation.geoLocation
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoLocationApiService {

    @GET("/tmap/geo/reversegeocoding")
    suspend fun getLocationInfo(
        @Query("appKey") appKey: String,
        @Query("addressType") addressType: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String
    ): NetworkResult<geoLocation>
}