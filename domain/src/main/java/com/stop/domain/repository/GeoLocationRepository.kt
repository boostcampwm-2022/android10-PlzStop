package com.stop.domain.repository

import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.geoLocation.GeoLocationInfo

interface GeoLocationRepository {

    suspend fun getGeoLocationInfo(
        appKey: String,
        addressType: AddressType,
        lat: String,
        lon: String
    ): GeoLocationInfo
}