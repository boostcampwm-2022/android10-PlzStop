package com.stop.data.repository

import com.stop.data.remote.source.route.RouteRemoteDataSource
import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.route.gyeonggi.GetGyeonggiBusLastTimeResponse
import com.stop.domain.model.route.gyeonggi.GetGyeonggiBusLineIdResponse
import com.stop.domain.model.route.gyeonggi.GetGyeonggiBusRouteStationsResponse
import com.stop.domain.model.route.gyeonggi.GetGyeonggiBusStationIdResponse
import com.stop.domain.model.route.seoul.bus.GetBusLineResponse
import com.stop.domain.model.route.seoul.bus.GetBusStationArsIdResponse
import com.stop.domain.model.route.seoul.subway.Station
import com.stop.domain.model.route.seoul.subway.StationLastTime
import com.stop.domain.model.route.seoul.subway.SubwayCircleType
import com.stop.domain.model.route.seoul.subway.WeekType
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.domain.model.route.tmap.origin.ReverseGeocodingResponse
import com.stop.domain.model.route.tmap.origin.RouteResponse
import com.stop.domain.repository.RouteRepository
import javax.inject.Inject

internal class RouteRepositoryImpl @Inject constructor(
    private val remoteDataSource: RouteRemoteDataSource
) : RouteRepository {

    override suspend fun getRoute(routeRequest: RouteRequest): RouteResponse {
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

    override suspend fun getSeoulBusStationArsId(stationName: String): GetBusStationArsIdResponse {
        return remoteDataSource.getSeoulBusStationArsId(stationName)
    }

    override suspend fun getSeoulBusLine(stationId: String): GetBusLineResponse {
        return remoteDataSource.getSeoulBusLine(stationId)
    }

    override suspend fun getSeoulBusLastTime(stationId: String, lineId: String): String? {
        return remoteDataSource.getSeoulBusLastTime(stationId, lineId)
            .lastTimeMsgBody
            .lastTimes
            .first()
            .lastTime
    }

    override suspend fun getGyeonggiBusStationId(stationName: String): GetGyeonggiBusStationIdResponse {
        return remoteDataSource.getGyeonggiBusStationId(stationName)
    }

    override suspend fun getGyeonggiBusLine(stationId: String): GetGyeonggiBusLineIdResponse {
        return remoteDataSource.getGyeongggiBusLine(stationId)
    }

    override suspend fun getGyeonggiBusLastTime(lineId: String): GetGyeonggiBusLastTimeResponse {
        return remoteDataSource.getGyeongggiBusLastTime(lineId)
    }

    override suspend fun getGyeonggiBusRouteStations(lineId: String): GetGyeonggiBusRouteStationsResponse {
        return remoteDataSource.getGyeonggiBusRouteStations(lineId)
    }
}