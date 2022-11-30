package com.stop.data.remote.source.route

import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.network.ApisDataService
import com.stop.data.remote.network.OpenApiSeoulService
import com.stop.data.remote.network.TmapApiService
import com.stop.data.remote.network.WsBusApiService
import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.route.gyeonggi.GyeonggiBusStation
import com.stop.domain.model.route.gyeonggi.GyeonggiBusRoute
import com.stop.domain.model.route.gyeonggi.GyeonggiBusLastTime
import com.stop.domain.model.route.seoul.bus.*
import com.stop.domain.model.route.seoul.subway.*
import com.stop.domain.model.route.seoul.subway.Station
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.domain.model.route.tmap.origin.*
import javax.inject.Inject

internal class RouteRemoteDataSourceImpl @Inject constructor(
    private val tmapApiService: TmapApiService,
//    private val fakeTmapApiService: FakeTmapApiService,
    private val openApiSeoulService: OpenApiSeoulService,
    private val wsBusApiService: WsBusApiService,
    private val apisDataService: ApisDataService,
) : RouteRemoteDataSource {

    override suspend fun getRoute(routeRequest: RouteRequest): RouteResponse {
        with(
            tmapApiService.getRoutes(routeRequest.toMap())
        ) {
            return when (this) {
                is NetworkResult.Success -> this.data
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun reverseGeocoding(
        coordinate: Coordinate,
        addressType: AddressType
    ): ReverseGeocodingResponse {
        with(
            tmapApiService.getReverseGeocoding(
                coordinate.latitude,
                coordinate.longitude,
                addressType = addressType.type
            )
        ) {
            return when (this) {
                is NetworkResult.Success -> this.data
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getSubwayStationCd(
        stationId: String,
        stationName: String,
    ): String {
        with(
            openApiSeoulService.getStationInfo(
                serviceName = "SearchInfoBySubwayNameService",
                stationName = stationName,
            )
        ) {
            return when (this) {
                is NetworkResult.Success -> findStationCd(stationId, this.data)
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getSubwayStations(lineName: String): List<Station> {
        with(
            openApiSeoulService.getSubwayStations(
                serviceName = "SearchSTNBySubwayLineInfo",
                lineName = lineName.padStart(2, '0') + "호선",
            )
        ) {
            return when (this) {
                is NetworkResult.Success -> this.data.searchStationNameBySubwayLineInfo.stations
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getSubwayStationLastTime(
        stationId: String,
        subwayCircleType: SubwayCircleType,
        weekType: WeekType,
    ): List<StationLastTime> {
        with(
            openApiSeoulService.getSubwayLastTime(
                serviceName = "SearchLastTrainTimeByIDService",
                stationId = stationId,
                weekTag = weekType.divisionValue,
                inOutTag = subwayCircleType.divisionValue,
            )
        ) {
            return when (this) {
                is NetworkResult.Success -> this.data.searchLastTrainTimeByIDService.stationLastTimes
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getSeoulBusStationArsId(stationName: String): List<BusStationInfo> {
        with(wsBusApiService.getBusArsId(stationName)) {
            return when (this) {
                is NetworkResult.Success -> this.data.arsIdMsgBody.busStations
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getSeoulBusRoute(stationId: String): List<BusRouteInfo> {
        with(wsBusApiService.getBusRoute(stationId)) {
            return when (this) {
                is NetworkResult.Success -> this.data.routeIdMsgBody.busRoutes
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getSeoulBusLastTime(
        stationId: String,
        lineId: String
    ): List<LastTimeInfo> {
        with(wsBusApiService.getBusLastTime(stationId, lineId)) {
            return when (this) {
                is NetworkResult.Success -> this.data.lastTimeMsgBody.lastTimes
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getGyeonggiBusStationId(stationName: String): List<GyeonggiBusStation> {
        with(apisDataService.getBusStationId(stationName)) {
            return when (this) {
                is NetworkResult.Success -> this.data.busStations.map { it.toDomain() }
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getGyeonggiBusRoute(stationId: String): List<GyeonggiBusRoute> {
        with(apisDataService.getBusRouteId(stationId)) {
            return when (this) {
                is NetworkResult.Success -> this.data.routes.map { it.toDomain() }
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getGyeonggiBusLastTime(lineId: String): List<GyeonggiBusLastTime> {
        with(apisDataService.getBusLastTime(lineId)) {
            return when (this) {
                is NetworkResult.Success -> this.data.lastTimes.map { it.toDomain() }
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getGyeonggiBusRouteStations(lineId: String): List<GyeonggiBusStation> {
        with(apisDataService.getBusRouteStations(lineId)) {
            return when (this) {
                is NetworkResult.Success -> this.data.stations.map { it.toDomain() }
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    private fun eraseDuplicateLeg(itineraries: List<Itinerary>): List<Itinerary> {
        return itineraries.map { itinerary ->
            var beforeInfo: Pair<String, Coordinate>? = null
            var subtractTime = 0.0
            var subtractDistance = 0.0

            val newLegs = itinerary.legs.fold(listOf<Leg>()) { legs, leg ->
                val current =
                    Pair(leg.mode, Coordinate(leg.start.lat.toString(), leg.start.lon.toString()))

                if (legs.isEmpty()) {
                    beforeInfo = current
                    return@fold legs + leg
                }

                if (beforeInfo == current) {
                    subtractDistance += leg.distance
                    subtractTime += leg.sectionTime
                    return@fold legs
                }
                beforeInfo = current
                legs + leg
            }

            with(itinerary) {
                Itinerary(
                    fare = fare,
                    legs = newLegs,
                    pathType = pathType,
                    totalDistance = totalDistance - subtractDistance,
                    totalTime = totalTime - subtractTime.toInt(),
                    transferCount = transferCount,
                    walkDistance = walkDistance,
                    walkTime = walkTime,
                )
            }
        }
    }

    private fun findStationCd(stationId: String, data: SubwayStationResponse): String {
        return data.searchInfoBySubwayNameService.row.firstOrNull {
            it.frCode == stationId
        }?.stationCd ?: throw IllegalArgumentException(NO_SUBWAY_STATION)
    }

    companion object {
        private const val NO_SUBWAY_STATION = "해당하는 지하철역이 없습니다."
    }
}