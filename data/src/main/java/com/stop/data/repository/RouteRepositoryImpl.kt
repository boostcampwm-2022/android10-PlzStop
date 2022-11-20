package com.stop.data.repository

import com.stop.data.remote.source.route.RouteRemoteDataSource
import com.stop.domain.model.route.gyeonggi.GetGyeonggiBusStationIdResponse
import com.stop.domain.model.route.seoul.bus.GetBusStationArsIdResponse
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.domain.model.route.tmap.origin.ReverseGeocodingResponse
import com.stop.domain.model.route.tmap.origin.RouteResponse
import com.stop.domain.repository.RouteRepository
import javax.inject.Inject

internal class RouteRepositoryImpl @Inject constructor(
    private val remoteDataSource: RouteRemoteDataSource
): RouteRepository {

    override suspend fun getRoute(routeRequest: RouteRequest): RouteResponse {
        return remoteDataSource.getRoute(routeRequest)
    }

    override suspend fun reverseGeocoding(coordinate: Coordinate): ReverseGeocodingResponse {
        return remoteDataSource.reverseGeocoding(coordinate)
    }

    override suspend fun getSubwayStationCd(stationId: String, stationName: String): String {
        return remoteDataSource.getSubwayStationCd(stationId, stationName)
    }

    override suspend fun getSeoulBusStationArsId(stationName: String): GetBusStationArsIdResponse {
        return remoteDataSource.getSeoulBusStationArsId(stationName)
    }

    override suspend fun getGyeonggiBusStationId(stationName: String): GetGyeonggiBusStationIdResponse {
        return remoteDataSource.getGyeonggiBusStationId(stationName)
    }
}