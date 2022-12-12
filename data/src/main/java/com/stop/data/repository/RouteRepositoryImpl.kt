package com.stop.data.repository

import com.squareup.moshi.JsonDataException
import com.stop.data.remote.source.route.RouteRemoteDataSource
import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.nowlocation.SubwayRouteLocationUseCaseItem
import com.stop.domain.model.route.gyeonggi.*
import com.stop.domain.model.route.seoul.bus.*
import com.stop.domain.model.route.seoul.subway.Station
import com.stop.domain.model.route.seoul.subway.StationLastTime
import com.stop.domain.model.route.seoul.subway.TransportDirectionType
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
        return try {
            remoteDataSource.getRoute(routeRequest)
        } catch (exception: JsonDataException) {
            listOf()
        }
    }

    override suspend fun reverseGeocoding(
        coordinate: Coordinate,
        addressType: AddressType
    ): ReverseGeocodingResponse {
        return remoteDataSource.reverseGeocoding(coordinate, addressType)
    }

    override suspend fun getSubwayStationCd(stationId: String, stationName: String): String {
        return try {
            remoteDataSource.getSubwayStationCd(stationId, stationName)
        } catch (exception: JsonDataException) {
            ""
        } catch (exception: IllegalArgumentException) {
            exception.printStackTrace()
            ""
        }
    }

    override suspend fun getSubwayStations(lineName: String): List<Station> {
        return try {
            remoteDataSource.getSubwayStations(lineName)
        } catch (exception: JsonDataException) {
            listOf()
        }
    }

    override suspend fun getSubwayStationLastTime(
        stationId: String,
        transportDirectionType: TransportDirectionType,
        weekType: WeekType,
    ): List<StationLastTime> {
        return try {
            remoteDataSource.getSubwayStationLastTime(stationId, transportDirectionType, weekType)
        } catch (exception: JsonDataException) {
            listOf()
        }
    }

    override suspend fun getSubwayRoute(
        routeRequest: RouteRequest,
        subwayLine: String,
        startSubwayStation: String,
        endSubwayStation: String
    ): SubwayRouteLocationUseCaseItem {
        return try {
            remoteDataSource.getRoute(routeRequest).first {
                it.legs.any { leg ->
                    leg.mode == "SUBWAY"
                            && leg.route?.contains(subwayLine) ?: false
                            && leg.start.name.contains(startSubwayStation)
                            && leg.end.name.contains(endSubwayStation)
                }
            }.legs.first { leg ->
                leg.mode == "SUBWAY"
                        && leg.route?.contains(subwayLine) ?: false
                        && leg.start.name.contains(startSubwayStation)
                        && leg.end.name.contains(endSubwayStation)
            }.toUseCaseModel()
        } catch (exception: JsonDataException) {
            throw IllegalArgumentException("경로 검색 결과가 없습니다.")
        }
    }

    override suspend fun getSeoulBusStationArsId(stationName: String): List<BusStationInfo> {
        return  try {
            remoteDataSource.getSeoulBusStationArsId(stationName)
        } catch (exception: JsonDataException) {
            listOf()
        }
    }

    override suspend fun getSeoulBusRoute(stationId: String): List<BusRouteInfo> {
        return try {
            remoteDataSource.getSeoulBusRoute(stationId)
        } catch (exception: JsonDataException) {
            listOf()
        }
    }

    override suspend fun getSeoulBusLastTime(stationId: String, lineId: String): List<LastTimeInfo> {
        return try{
            remoteDataSource.getSeoulBusLastTime(stationId, lineId)
        } catch (exception: JsonDataException) {
            listOf()
        }
    }

    override suspend fun getGyeonggiBusStationId(stationName: String): List<GyeonggiBusStation> {
        return try {
            remoteDataSource.getGyeonggiBusStationId(stationName)
        } catch (exception: NullPointerException) {
            listOf()
        }
    }

    override suspend fun getGyeonggiBusRoute(stationId: String): List<GyeonggiBusRoute> {
        return try {
            remoteDataSource.getGyeonggiBusRoute(stationId)
        } catch (exception: NullPointerException) {
            listOf()
        }
    }

    override suspend fun getGyeonggiBusLastTime(lineId: String): List<GyeonggiBusLastTime> {
        return try {
            remoteDataSource.getGyeonggiBusLastTime(lineId)
        } catch (exception: NullPointerException) {
            listOf()
        }
    }

    override suspend fun getGyeonggiBusRouteStations(lineId: String): List<GyeonggiBusStation> {
        return try {
            remoteDataSource.getGyeonggiBusRouteStations(lineId)
        } catch (exception: NullPointerException) {
            listOf()
        }
    }
}