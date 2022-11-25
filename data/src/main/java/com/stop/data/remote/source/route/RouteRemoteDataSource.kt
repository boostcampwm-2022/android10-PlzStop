package com.stop.data.remote.source.route

import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.route.gyeonggi.GetGyeonggiBusStationIdResponse
import com.stop.domain.model.route.seoul.bus.GetBusStationArsIdResponse
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.domain.model.route.tmap.origin.ReverseGeocodingResponse
import com.stop.domain.model.route.tmap.origin.RouteResponse

internal interface RouteRemoteDataSource {

    suspend fun getRoute(routeRequest: RouteRequest): RouteResponse

    suspend fun reverseGeocoding(coordinate: Coordinate, addressType: AddressType): ReverseGeocodingResponse

    suspend fun getSubwayStationCd(stationId: String, stationName: String): String

    suspend fun getSeoulBusStationArsId(stationName: String): GetBusStationArsIdResponse

    suspend fun getGyeonggiBusStationId(stationName: String): GetGyeonggiBusStationIdResponse
}