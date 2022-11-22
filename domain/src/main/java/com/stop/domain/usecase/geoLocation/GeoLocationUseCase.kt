package com.stop.domain.usecase.geoLocation

import com.stop.domain.model.geoLocation.GeoLocationInfo

interface GeoLocationUseCase {

    suspend fun getGeoLocationInfo(
        lat: Double,
        lon: Double
    ): GeoLocationInfo
}