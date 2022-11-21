package com.stop.data.remote.network

import com.stop.data.remote.model.NetworkResult
import com.stop.domain.model.route.tmap.origin.ReverseGeocodingResponse
import com.stop.domain.model.route.tmap.origin.RouteResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface FakeTmapApiService {

    @FormUrlEncoded
    @POST(TRANSPORT_URL)
    suspend fun getRoutes(
        @Field("startX") startX: String,
        @Field("startY") startY: String,
        @Field("endX") endX: String,
        @Field("endY") endY: String,
        @Field("lang") lang: Int,
        @Field("format") format: String,
        @Field("count") count: Int,
    ): NetworkResult<RouteResponse>

    @GET(REVERSE_GEOCODING_URL)
    suspend fun getReverseGeocoding(
        @Query("lat") latitude: String,
        @Query("lon") longitude: String,
        @Query("version") version: Int = REVERSE_GEOCODING_VERSION,
        @Query("coordType") coordinationType: String = REVERSE_GEOCODING_COORDINATION_TYPE,
        @Query("addressType") addressType: String = REVERSE_GEOCODING_ADDRESS_TYPE,
    ): NetworkResult<ReverseGeocodingResponse>

    companion object {
        private const val TRANSPORT_URL = "transit/routes"

        private const val REVERSE_GEOCODING_URL = "tmap/geo/reversegeocoding"
        private const val REVERSE_GEOCODING_VERSION = 1
        private const val REVERSE_GEOCODING_COORDINATION_TYPE = "WGS84GEO"
        private const val REVERSE_GEOCODING_ADDRESS_TYPE = "A02"
    }
}