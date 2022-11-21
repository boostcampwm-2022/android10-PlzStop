package com.stop.data.remote.source.route

import com.stop.data.BuildConfig
import com.stop.data.remote.model.NetworkResult
import com.stop.data.remote.network.ApisDataService
import com.stop.data.remote.network.FakeTmapApiService
import com.stop.data.remote.network.OpenApiSeoulService
import com.stop.data.remote.network.WsBusApiService
import com.stop.domain.model.route.gyeonggi.GetGyeonggiBusStationIdResponse
import com.stop.domain.model.route.seoul.bus.GetBusStationArsIdResponse
import com.stop.domain.model.route.seoul.subway.SubwayStationResponse
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.domain.model.route.tmap.origin.ReverseGeocodingResponse
import com.stop.domain.model.route.tmap.origin.RouteResponse
import javax.inject.Inject

internal class RouteRemoteDataSourceImpl @Inject constructor(
//    private val tmapApiService: TmapApiService,
    private val fakeTmapApiService: FakeTmapApiService,
    private val openApiSeoulService: OpenApiSeoulService,
    private val wsBusApiService: WsBusApiService,
    private val apisDataService: ApisDataService,
) : RouteRemoteDataSource {

    override suspend fun getRoute(routeRequest: RouteRequest): RouteResponse {
        with(
            fakeTmapApiService.getRoutes(
                startX = routeRequest.startX,
                startY = routeRequest.startY,
                endX = routeRequest.endX,
                endY = routeRequest.endY,
                lang = routeRequest.lang,
                format = routeRequest.format,
                count = routeRequest.count,
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

    override suspend fun reverseGeocoding(coordinate: Coordinate): ReverseGeocodingResponse {
        with(fakeTmapApiService.getReverseGeocoding(coordinate.latitude, coordinate.longitude)) {
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
        with(openApiSeoulService.getStationInfo(createOpenApiSeoulUrl(stationName))) {
            return when (this) {
                is NetworkResult.Success -> findStationCd(stationId, this.data)
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getSeoulBusStationArsId(stationName: String): GetBusStationArsIdResponse {
        with(wsBusApiService.getBusArsId(createWsBusUrl(GET_BUS_ARS_URL, stationName))) {
            return when (this) {
                is NetworkResult.Success -> this.data
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    override suspend fun getGyeonggiBusStationId(stationName: String): GetGyeonggiBusStationIdResponse {
        with(apisDataService.getBusStationId(createGyeonggiBusStationIdUrl(stationName))) {
            return when (this) {
                is NetworkResult.Success -> this.data
                is NetworkResult.Failure -> throw IllegalArgumentException(this.message)
                is NetworkResult.NetworkError -> throw this.exception
                is NetworkResult.Unexpected -> throw this.exception
            }
        }
    }

    private fun createGyeonggiBusStationIdUrl(stationName: String): String {
        return "$APIS_DATA_URL$GET_GYEONGGI_BUS_STATION_ID_URL?ServiceKey=${BuildConfig.WS_BUS_KEY}&keyword=$stationName"
    }

    private fun createWsBusUrl(apiName: String, stationName: String): String {
        return "$WS_BUS_URL$apiName?serviceKey=${BuildConfig.WS_BUS_KEY}&stSrch=$stationName&resultType=json"
    }

    private fun createOpenApiSeoulUrl(stationName: String): String {
        return "$OPEN_API_SEOUL_URL${BuildConfig.OPEN_API_SEOUL_KEY}/json/SearchInfoBySubwayNameService/1/5/$stationName/"
    }

    private fun findStationCd(stationId: String, data: SubwayStationResponse): String {
        return data.searchInfoBySubwayNameService.row.firstOrNull {
            it.frCode == stationId
        }?.stationCd ?: throw IllegalArgumentException(NO_SUBWAY_STATION)
    }

    companion object {
        private const val OPEN_API_SEOUL_URL = "http://openAPI.seoul.go.kr:8088/"
        private const val WS_BUS_URL = "http://ws.bus.go.kr/api/rest/"

        //        private const val APIS_DATA_URL = "https://apis.data.go.kr/6410000/"
        private const val APIS_DATA_URL = "http://apis.data.go.kr/6410000/"

        private const val GET_GYEONGGI_BUS_STATION_ID_URL = "busstationservice/getBusStationList"
        private const val GET_BUS_ARS_URL = "stationinfo/getStationByName"

        private const val NO_SUBWAY_STATION = "해당하는 지하철역이 없습니다."
    }
}