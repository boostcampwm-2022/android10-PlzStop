package com.stop.domain.usecase.geoLocation

import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.geoLocation.GeoLocationInfo
import com.stop.domain.repository.GeoLocationRepository
import javax.inject.Inject

class GeoLocationUseCaseImpl @Inject constructor(
    private val geoLocationRepository: GeoLocationRepository
) : GeoLocationUseCase {

    override suspend fun getGeoLocationInfo(
        appKey: String,
        addressType: AddressType,
        lat: String,
        lon: String
    ): GeoLocationInfo {
        return geoLocationRepository.getGeoLocationInfo(
            appKey,
            addressType,
            lat,
            lon
        )
    }
}