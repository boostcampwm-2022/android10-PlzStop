package com.stop.data.repository

import com.stop.data.remote.source.route.RouteRemoteDataSource
import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.route.gyeonggi.*
import com.stop.domain.model.route.seoul.bus.*
import com.stop.domain.model.route.seoul.subway.Station
import com.stop.domain.model.route.seoul.subway.StationLastTime
import com.stop.domain.model.route.seoul.subway.SubwayCircleType
import com.stop.domain.model.route.seoul.subway.WeekType
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.domain.model.route.tmap.origin.Itinerary
import com.stop.domain.model.route.tmap.origin.ReverseGeocodingResponse
import com.stop.domain.repository.RouteRepository
import javax.inject.Inject

internal class RouteRepositoryImpl @Inject constructor(
    private val remoteDataSource: RouteRemoteDataSource
) : RouteRepository {

    override suspend fun getRoute(routeRequest: RouteRequest): List<Itinerary> {
        return remoteDataSource.getRoute(routeRequest)
    }

    override suspend fun reverseGeocoding(
        coordinate: Coordinate,
        addressType: AddressType
    ): ReverseGeocodingResponse {
        return remoteDataSource.reverseGeocoding(coordinate, addressType)
    }

    override suspend fun getSubwayStationCd(stationId: String, stationName: String): String {
        return remoteDataSource.getSubwayStationCd(stationId, stationName)
    }

    override suspend fun getSubwayStations(lineName: String): List<Station> {
        return remoteDataSource.getSubwayStations(lineName)
    }

    override suspend fun getSubwayStationLastTime(
        stationId: String,
        subwayCircleType: SubwayCircleType,
        weekType: WeekType,
    ): List<StationLastTime> {
        return remoteDataSource.getSubwayStationLastTime(stationId, subwayCircleType, weekType)
    }

    override suspend fun getSeoulBusStationArsId(stationName: String): List<BusStationInfo> {
        return remoteDataSource.getSeoulBusStationArsId(stationName)
    }

    override suspend fun getSeoulBusRoute(stationId: String): List<BusRouteInfo> {
        return remoteDataSource.getSeoulBusRoute(stationId)
    }

    override suspend fun getSeoulBusLastTime(stationId: String, lineId: String): List<LastTimeInfo> {
        return remoteDataSource.getSeoulBusLastTime(stationId, lineId)
    }

    override suspend fun getGyeonggiBusStationId(stationName: String): List<GyeonggiBusStation> {
        return remoteDataSource.getGyeonggiBusStationId(stationName)
    }

    override suspend fun getGyeonggiBusRoute(stationId: String): List<GyeonggiBusRoute> {
        return remoteDataSource.getGyeonggiBusRoute(stationId)
    }

    override suspend fun getGyeonggiBusLastTime(lineId: String): List<GyeonggiBusLastTime> {
        return remoteDataSource.getGyeonggiBusLastTime(lineId)
    }

    override suspend fun getGyeonggiBusRouteStations(lineId: String): List<GyeonggiBusStation> {
        return remoteDataSource.getGyeonggiBusRouteStations(lineId)
    }
}