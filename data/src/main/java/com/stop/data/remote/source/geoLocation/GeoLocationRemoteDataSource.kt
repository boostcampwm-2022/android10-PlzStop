package com.stop.data.remote.source.geoLocation

import com.stop.data.remote.model.geoLocation.AddressInfo
import com.stop.domain.model.geoLocation.AddressType

interface GeoLocationRemoteDataSource {
    suspend fun getGeoLocationInfo(
        appKey: String,
        addressType: AddressType,
        lat: String,
        lon: String
    ): AddressInfo
}