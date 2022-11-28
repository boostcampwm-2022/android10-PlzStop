package com.stop.data.remote.source.route

import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.route.gyeonggi.GetGyeonggiBusLastTimeResponse
import com.stop.domain.model.route.gyeonggi.GetGyeonggiBusLineIdResponse
import com.stop.domain.model.route.gyeonggi.GetGyeonggiBusRouteStationsResponse
import com.stop.domain.model.route.gyeonggi.GetGyeonggiBusStationIdResponse
import com.stop.domain.model.route.seoul.bus.GetBusLastTimeResponse
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

internal interface RouteRemoteDataSource {

    suspend fun getRoute(routeRequest: RouteRequest): RouteResponse

    suspend fun reverseGeocoding(coordinate: Coordinate, addressType: AddressType): ReverseGeocodingResponse

    suspend fun getSubwayStationCd(stationId: String, stationName: String): String

    suspend fun getSubwayStations(lineName: String): List<Station>

    suspend fun getSubwayStationLastTime(
        stationId: String,
        subwayCircleType: SubwayCircleType,
        weekType: WeekType,
    ): List<StationLastTime>

    suspend fun getSeoulBusStationArsId(stationName: String): GetBusStationArsIdResponse

    suspend fun getSeoulBusLine(stationId: String): GetBusLineResponse

    suspend fun getSeoulBusLastTime(stationId: String, lineId: String): GetBusLastTimeResponse

    suspend fun getGyeonggiBusStationId(stationName: String): GetGyeonggiBusStationIdResponse

    suspend fun getGyeongggiBusLine(stationId: String): GetGyeonggiBusLineIdResponse

    suspend fun getGyeongggiBusLastTime(lineId: String): GetGyeonggiBusLastTimeResponse

    suspend fun getGyeonggiBusRouteStations(lineId: String): GetGyeonggiBusRouteStationsResponse
}