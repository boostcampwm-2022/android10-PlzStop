package com.stop.data.remote.source.geoLocation

import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.model.geoLocation.addressInfo
import com.stop.data.remote.network.GeoLocationApiService
import javax.inject.Inject

class GeoLocationRemoteDataSourceImpl @Inject constructor(
    private val geoLocationApiService: GeoLocationApiService
) : GeoLocationRemoteDataSource {
    
    override suspend fun getGeoLocationInfo(
        appKey: String,
        addressType: String,
        lat: String,
        lon: String
    ): addressInfo {
        val result = geoLocationApiService.getLocationInfo(
            appKey,
            addressType,
            lat,
            lon
        )

        when (result) {
            is NetworkResult.Error -> {
                throw Exception(result.message)
            }
            is NetworkResult.Success -> {
                return result.data?.addressInfo ?: addressInfo(
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
                )
            }
        }
    }
}