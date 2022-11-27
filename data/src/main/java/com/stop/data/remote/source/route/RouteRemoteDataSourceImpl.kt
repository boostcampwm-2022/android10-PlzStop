package com.stop.data.remote.source.route

import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.network.ApisDataService
import com.stop.data.remote.network.OpenApiSeoulService
import com.stop.data.remote.network.TmapApiService
import com.stop.data.remote.network.WsBusApiService
import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.route.gyeonggi.GetGyeonggiBusLineIdResponse
import com.stop.domain.model.route.gyeonggi.GetGyeonggiBusStationIdResponse
import com.stop.domain.model.route.seoul.bus.GetBusLastTimeResponse
import com.stop.domain.model.route.seoul.bus.GetBusLineResponse
import com.stop.domain.model.route.seoul.bus.GetBusStationArsIdResponse
import com.stop.domain.model.route.seoul.subway.*
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.domain.model.route.tmap.origin.ReverseGeocodingResponse
import com.stop.domain.model.route.tmap.origin.RouteResponse
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
                lineName = lineName,
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

    override suspend fun getSeoulBusStationArsId(stationName: String): GetBusStationArsIdResponse {
        with(wsBusApiService.getBusArsId(stationName)) {
            return when (this) {
                is NetworkResult.Success -> this.data
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getSeoulBusLine(stationId: String): GetBusLineResponse {
        with(wsBusApiService.getBusLine(stationId)) {
            return when (this) {
                is NetworkResult.Success -> this.data
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getSeoulBusLastTime(
        stationId: String,
        lineId: String
    ): GetBusLastTimeResponse {
        with(wsBusApiService.getBusLastTime(stationId, lineId)) {
            return when (this) {
                is NetworkResult.Success -> this.data
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getGyeonggiBusStationId(stationName: String): GetGyeonggiBusStationIdResponse {
        with(apisDataService.getBusStationId(stationName)) {
            return when (this) {
                is NetworkResult.Success -> this.data.toDomain()
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getGyeongggiBusLine(stationId: String): GetGyeonggiBusLineIdResponse {
        with(apisDataService.getBusLineId(stationId)) {
            return when (this) {
                is NetworkResult.Success -> this.data.toDomain()
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
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