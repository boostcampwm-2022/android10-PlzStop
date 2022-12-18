package com.stop.domain.usecase.geoLocation

import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.geoLocation.GeoLocationInfo
import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.domain.model.route.tmap.origin.AddressInfo
import com.stop.domain.repository.RouteRepository
import javax.inject.Inject

class GeoLocationUseCaseImpl @Inject constructor(
    private val routeRepository: RouteRepository
) : GeoLocationUseCase {

    override suspend operator fun invoke(lat: Double, lon: Double): GeoLocationInfo {
        val result = routeRepository.reverseGeocoding(
            Coordinate(lat.toString(), lon.toString()),
            AddressType.FULL_ADDRESS
        ).addressInfo
        val address = result.fullAddress.split(",").drop(1)

        return GeoLocationInfo(
            title = getTitle(result),
            roadAddress = address.first(),
            lotAddress = address.last().replace(result.buildingName, ""),
            distance = result.mappingDistance
        )
    }

    private fun getTitle(result: AddressInfo): String {
        return if (result.buildingName != "") {
            result.buildingName
        } else {
            result.fullAddress.split(",").drop(1).first()
        }
    }

}