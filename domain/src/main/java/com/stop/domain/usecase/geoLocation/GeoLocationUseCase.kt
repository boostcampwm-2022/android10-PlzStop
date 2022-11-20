package com.stop.domain.usecase.geoLocation

import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.geoLocation.GeoLocationInfo

interface GeoLocationUseCase {

    suspend fun getGeoLocationInfo(
        appKey: String,
        addressType: AddressType,
        lat: String,
        lon: String
    ): GeoLocationInfo
}