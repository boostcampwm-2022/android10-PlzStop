package com.stop.domain.usecase.route

import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.route.TransportIdRequest
import com.stop.domain.model.route.TransportLastTimeInfo
import com.stop.domain.model.route.TransportMoveType
import com.stop.domain.model.route.gyeonggi.GyeonggiBusStation
import com.stop.domain.model.route.seoul.bus.BusStationInfo
import com.stop.domain.model.route.tmap.custom.*
import com.stop.domain.repository.RouteRepository
import javax.inject.Inject
import kotlin.math.abs

internal class GetLastTransportTimeUseCaseImpl @Inject constructor(
    private val routeRepository: RouteRepository
) : GetLastTransportTimeUseCase {

    class NoAppropriateDataException(override val message: String): Exception()

    private val allowedSubwayLineForUse = (SUBWAY_LINE_ONE..SUBWAY_LINE_EIGHT).toList()

    override suspend fun getLastTransportTime(itinerary: Itinerary): TransportLastTimeInfo? {
        // 승차지, 도착지, 고유 번호를 알아내는데 필요한 정보로만 구성된 데이터 클래스로 변환하기
        val transportIdRequests =
            itinerary.routes.fold(listOf<TransportIdRequest>()) { transportIdRequests, route ->
                when (route) {
                    is WalkRoute -> transportIdRequests
                    is TransportRoute -> {
                        val startStation = route.stations.first()
                        val transportMoveType = TransportMoveType.getMoveTypeByName(route.mode.name)
                            ?: return@fold transportIdRequests

                        transportIdRequests + TransportIdRequest(
                            transportMoveType = transportMoveType,
                            id = startStation.stationId,
                            stationName = startStation.stationName,
                            coordinate = startStation.coordinate,
                            stationType = route.routeType,
                        )
                    }
                    else -> transportIdRequests
                }
            }

        // 공공데이터 포털에서 사용하는 버스, 지하철의 고유번호로 변환하는 작업
        val transportLastTimeRequests = transportIdRequests.map { transportIdRequest ->
            try {
                transportIdRequest.changeId(getId(transportIdRequest))
            } catch (exception: NoAppropriateDataException) {
                exception.printStackTrace()
                return null
            }
        }

        // 고유 번호로 승차지의 막차 시간 모두 알아내기
//        val dataWithLastTime: List<Unit>

        // 막차 시간 중 가장 빠른 시간과 dataWithLastTime을 가지는 데이터 클래스 반환하기
//        return ReturnData(fastestTime, dataWithLastTime)
        return null
    }

    private suspend fun getId(
        transportIdRequest: TransportIdRequest,
    ): String {
        return when (transportIdRequest.transportMoveType) {
            TransportMoveType.SUBWAY -> {
                if (allowedSubwayLineForUse.contains(transportIdRequest.stationType).not()) {
                    throw NoAppropriateDataException(NOT_REGISTERED_STATION)
                }
                routeRepository.getSubwayStationCd(transportIdRequest.id, transportIdRequest.stationName)
            }
            TransportMoveType.BUS -> {
                getBusIdUsedAtPublicApi(transportIdRequest)
            }
        }

    }

    /**
     * 버스의 막차 시간, 배차 시간 조회에 필요한 ID를 구합니다.
     * T MAP은 정류소의 정확한 좌표를 찍어주지만, 공공데이터는 정류소 주변부 좌표를 보내주기 때문에
     * 좌표가 가장 근접한 것을 선택해야 합니다.
     * 서울 버스 API에 경기도 정류소를 조회하면 arsId가 0으로 나옵니다.
     * arsID가 0인 경우 서울 버스를 경기도에서, 경기도 버스를 서울에서 검색한 건 아닌지 확인해주세요.
     */
    private suspend fun getBusIdUsedAtPublicApi(
        transportIdRequest: TransportIdRequest
    ): String {
        val reverseGeocodingResponse =
            routeRepository.reverseGeocoding(transportIdRequest.coordinate, AddressType.LOT_ADDRESS)

        val arsId = when (reverseGeocodingResponse.addressInfo.cityDo) {
            GYEONGGI_DO -> {
                if (transportIdRequest.stationName.contains("마을")) {
                    throw NoAppropriateDataException(GYEONGGI_REGION_BUS_NOT_SUPPORT)
                }
                val busStations =
                    routeRepository.getGyeonggiBusStationId(transportIdRequest.stationName).msgBody.busStations

                findClosestGyeonggiBusStation(transportIdRequest, busStations)
            }
            SEOUL -> {
                val busStations =
                    routeRepository.getSeoulBusStationArsId(transportIdRequest.stationName).msgBody.busStations

                findClosestSeoulBusStation(transportIdRequest, busStations)
            }
            else -> throw NoAppropriateDataException(NO_SUPPORTING_CITY)
        }

        if (arsId == UNKNOWN_ARS_ID) {
            throw NoAppropriateDataException(NO_BUS_ARS_ID)
        }
        return arsId
    }

    private fun findClosestSeoulBusStation(
        transportIdRequest: TransportIdRequest,
        busStations: List<BusStationInfo>,
    ): String {
        val originLongitude = correctLongitudeValue(transportIdRequest.coordinate.longitude)
        val originLatitude = correctLatitudeValue(transportIdRequest.coordinate.longitude)
        var closestStation: BusStationInfo? = null
        var closestDistance = 0

        busStations.filter {
            it.stationName == transportIdRequest.stationName
        }.map {
            if (closestStation == null) {
                closestStation = it
                val x = abs(originLongitude - correctLongitudeValue(it.longitude))
                val y = abs(originLatitude - correctLatitudeValue(it.latitude))
                closestDistance = x * x + y * y
                return@map
            }

            val x = abs(originLongitude - correctLongitudeValue(it.longitude))
            val y = abs(originLatitude - correctLatitudeValue(it.latitude))
            val distance = x * x + y * y

            if (distance < closestDistance) {
                closestDistance = distance
                closestStation = it
            }
        }

        return closestStation?.arsId ?: UNKNOWN_ARS_ID
    }

    private fun findClosestGyeonggiBusStation(
        transportIdRequest: TransportIdRequest,
        busStations: List<GyeonggiBusStation>,
    ): String {
        val originLongitude = correctLongitudeValue(transportIdRequest.coordinate.longitude)
        val originLatitude = correctLatitudeValue(transportIdRequest.coordinate.latitude)
        var closestStation: GyeonggiBusStation? = null
        var closestDistance = 0

        busStations.filter {
            it.stationName == transportIdRequest.stationName
        }.map {
            if (closestStation == null) {
                closestStation = it
                val x = abs(originLongitude - correctLongitudeValue(it.longitude))
                val y = abs(originLatitude - correctLatitudeValue(it.latitude))
                closestDistance = x * x + y * y
                return@map
            }

            val x = abs(originLongitude - correctLongitudeValue(it.longitude))
            val y = abs(originLatitude - correctLatitudeValue(it.latitude))
            val distance = x * x + y * y

            if (distance < closestDistance) {
                closestDistance = distance
                closestStation = it
            }
        }

        return closestStation?.stationId?.toString() ?: UNKNOWN_ARS_ID
    }

    private fun correctLongitudeValue(longitude: String): Int {
        return ((longitude.toDouble() - KOREA_LONGITUDE) * CORRECTION_VALUE).toInt()
    }

    private fun correctLatitudeValue(latitude: String): Int {
        return ((latitude.toDouble() - KOREA_LATITUDE) * CORRECTION_VALUE).toInt()
    }

    companion object {
        private const val NOT_REGISTERED_STATION = "API를 지원하지 않는 전철역입니다."
        private const val NO_BUS_ARS_ID = "버스 정류소 고유 아이디가 없습니다."
        private const val NO_SUPPORTING_CITY = "지원하지 않는 도시입니다."
        private const val GYEONGGI_REGION_BUS_NOT_SUPPORT = "경기도 마을 버스 정보는 API에서 제공하지 않습니다."

        private const val UNKNOWN_ARS_ID = "0"
        private const val KOREA_LONGITUDE = 127
        private const val KOREA_LATITUDE = 37
        private const val CORRECTION_VALUE = 10_000
        private const val GYEONGGI_DO = "경기도"
        private const val SEOUL = "서울특별시"

        private const val SUBWAY_LINE_ONE = 1
        private const val SUBWAY_LINE_EIGHT = 8
    }
}