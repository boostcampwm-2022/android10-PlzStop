package com.stop.domain.usecase.geoLocation

import com.stop.domain.model.geoLocation.GeoLocationInfo

interface GeoLocationUseCase {

    suspend operator fun invoke(
        lat: Double,
        lon: Double
    ): GeoLocationInfo

}