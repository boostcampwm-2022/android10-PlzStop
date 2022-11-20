package com.stop.data.repository

import com.stop.data.remote.source.geoLocation.GeoLocationRemoteDataSource
import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.geoLocation.GeoLocationInfo
import com.stop.domain.repository.GeoLocationRepository
import javax.inject.Inject

class GeoLocationRepositoryImpl @Inject constructor(
    private val geoLocationRemoteDataSource: GeoLocationRemoteDataSource
) : GeoLocationRepository {

    override suspend fun getGeoLocationInfo(
        appKey: String,
        addressType: AddressType,
        lat: String,
        lon: String
    ): GeoLocationInfo {
        return geoLocationRemoteDataSource.getGeoLocationInfo(
            appKey,
            addressType,
            lat,
            lon
        ).toRepositoryModel()
    }
}