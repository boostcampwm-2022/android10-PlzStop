package com.stop.data.remote.source.geoLocation

import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.model.geoLocation.AddressInfo
import com.stop.data.remote.network.GeoLocationApiService
import com.stop.domain.model.geoLocation.AddressType
import javax.inject.Inject

class GeoLocationRemoteDataSourceImpl @Inject constructor(
    private val geoLocationApiService: GeoLocationApiService
) : GeoLocationRemoteDataSource {

    override suspend fun getGeoLocationInfo(
        appKey: String,
        addressType: AddressType,
        lat: String,
        lon: String
    ): AddressInfo {
        val result = geoLocationApiService.getLocationInfo(
            appKey,
            addressType.type,
            lat,
            lon
        )

        when (result) {
            is NetworkResult.Error -> {
                throw Exception(result.message)
            }
            is NetworkResult.Success -> {
                return result.data?.addressInfo ?: AddressInfo(
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