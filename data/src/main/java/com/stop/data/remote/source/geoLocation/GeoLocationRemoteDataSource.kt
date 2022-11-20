package com.stop.data.remote.source.geoLocation

import com.stop.data.remote.model.geoLocation.addressInfo

interface GeoLocationRemoteDataSource {
    suspend fun getGeoLocationInfo(
        appKey: String,
        addressType: String,
        lat: String,
        lon: String
    ): addressInfo
}