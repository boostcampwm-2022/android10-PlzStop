package com.stop.domain.usecase.route

import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.route.Area
import com.stop.domain.model.route.TransportIdRequest
import com.stop.domain.model.route.TransportLastTimeInfo
import com.stop.domain.model.route.TransportMoveType
import com.stop.domain.model.route.gyeonggi.GyeonggiBusStation
import com.stop.domain.model.route.seoul.bus.BusStationInfo
import com.stop.domain.model.route.seoul.subway.Station
import com.stop.domain.model.route.seoul.subway.SubwayCircleType
import com.stop.domain.model.route.seoul.subway.WeekType
import com.stop.domain.model.route.tmap.custom.*
import com.stop.domain.repository.RouteRepository
import javax.inject.Inject
import kotlin.math.abs

internal class GetLastTransportTimeUseCaseImpl @Inject constructor(
    private val routeRepository: RouteRepository
) : GetLastTransportTimeUseCase {

    class NoAppropriateDataException(override val message: String) : Exception()
    class ApiServerDataException(override val message: String) : Exception()

    private val allowedSubwayLineForUse = (SUBWAY_LINE_ONE..SUBWAY_LINE_EIGHT).toList()

    override suspend fun invoke(itinerary: Itinerary): TransportLastTimeInfo? {
        // 승차지, 도착지, 고유 번호를 알아내는데 필요한 정보로만 구성된 데이터 클래스로 변환하기
        var transportIdRequests =
            itinerary.routes.fold(listOf<TransportIdRequest>()) { transportIdRequests, route ->
                when (route) {
                    is WalkRoute -> transportIdRequests
                    is TransportRoute -> {
                        val startStation = route.stations.first()
                        val transportMoveType = TransportMoveType.getMoveTypeByName(route.mode.name)
                            ?: return@fold transportIdRequests

                        transportIdRequests + TransportIdRequest(
                            transportMoveType = transportMoveType,
                            stationId = startStation.stationId,
                            stationName = startStation.stationName,
                            coordinate = startStation.coordinate,
                            stationType = route.routeType,
                            area = getArea(startStation.coordinate),
                            lineName = route.routeInfo,
                            lineId = UNKNOWN_ID,
                            term = NOT_YET_CALCULATED,
                            destinationStation = route.end,
                            destinationStationId = UNKNOWN_ID,
                        )
                    }
                    else -> transportIdRequests
                }
            }

        // 공공데이터 포털에서 사용하는 버스 정류소, 지하철 역의 고유번호로 변환하는 작업
        transportIdRequests = transportIdRequests.map { transportIdRequest ->
            try {
                getStationId(transportIdRequest)
            } catch (exception: NoAppropriateDataException) {
                exception.printStackTrace()
                return null
            }
        }

        // 공공데이터 포털에서 사용하는 버스 노선, 지하철 역의 노선번호로 변환하는 작업
        transportIdRequests = transportIdRequests.map { transportIdRequest ->
            try {
                when (transportIdRequest.transportMoveType) {
                    TransportMoveType.BUS -> {
                        when (transportIdRequest.area) {
                            Area.SEOUL -> getSeoulBusLineId(transportIdRequest)
                            Area.GYEONGGI -> getGyeongggiBusLineId(transportIdRequest)
                            Area.UN_SUPPORT_AREA -> throw NoAppropriateDataException(UN_SUPPORT_AREA)
                        }
                    }
                    TransportMoveType.SUBWAY -> transportIdRequest
                }
            } catch (exception: NoAppropriateDataException) {
                exception.printStackTrace()
                return null
            }
        }


        // 고유 번호로 승차지의 막차 시간 모두 알아내기
        val dataWithLastTime: List<String?> = transportIdRequests.map { transportIdRequest ->
            try {
                when (transportIdRequest.transportMoveType) {
                    TransportMoveType.BUS -> getBusLastTransportTime(transportIdRequest)
                    TransportMoveType.SUBWAY -> getSubwayLastTransportTime(transportIdRequest)
                }
            } catch (exception: ApiServerDataException) {
                exception.printStackTrace()
                null
            }
        }

        // 막차 시간 중 가장 빠른 시간과 dataWithLastTime을 가지는 데이터 클래스 반환하기
//        return ReturnData(fastestTime, dataWithLastTime)
        return null
    }

    private suspend fun getGyeongggiBusLineId(transportIdRequest: TransportIdRequest): TransportIdRequest {
        val busName = transportIdRequest.lineName.split(":")[1]
        val line =
            routeRepository.getGyeonggiBusLine(transportIdRequest.stationId).msgBody.routeList.firstOrNull {
                it.busName.contains(busName)
            } ?: throw NoAppropriateDataException(NO_BUS_LINE_ID)

        return transportIdRequest.changeLineId(line.routeId, null)
    }

    private suspend fun getSubwayLastTransportTime(transportIdRequest: TransportIdRequest): String {
        // "호선"을 붙이는 것을 Domain이 해야할까?
        val requestLine = transportIdRequest.stationType
            .toString().padStart(2, '0') + "호선"

        val stationsOfLine =
            routeRepository.getSubwayStations(requestLine).sortedWith(compareBy { it.frCode })

        val startStationIndex = stationsOfLine.indexOfFirst {
            it.stationName == transportIdRequest.stationName
        }
        if (startStationIndex == -1) {
            throw NoAppropriateDataException(NO_SUBWAY_STATION)
        }

        val endStationIndex = stationsOfLine.indexOfFirst {
            it.stationName == transportIdRequest.destinationStation.name
        }
        if (endStationIndex == -1) {
            throw NoAppropriateDataException(NO_SUBWAY_STATION)
        }

        // 내선, 외선 여부 확인
        // 2호선만 FR_CODE가 감소하면 외선, 그 외 1, 3 ~ 7호선은 FR_CODE가 증가하면 외선
        val subwayCircleType = checkInnerOrOuter(
            transportIdRequest.stationType,
            startStationIndex,
            endStationIndex,
            stationsOfLine
        )

        val weekType = whaWeekToday()

        val enableDestinationStation = if (startStationIndex < endStationIndex) {
            stationsOfLine.subList(startStationIndex, endStationIndex)
        } else {
            stationsOfLine.subList(endStationIndex + 1, startStationIndex + 1)
        }.map { it.stationName }

        val lastTrainTime = routeRepository.getSubwayStationLastTime(
            transportIdRequest.stationId,
            subwayCircleType,
            weekType
        )
        val suffix = checkStationCase(
            transportIdRequest.stationType,
            subwayCircleType,
            startStationIndex,
            endStationIndex,
            stationsOfLine
        )

        /**
         * suffix: True
         * true -> true
         * false -> false
         *
         * suffix: False
         * true -> false
         * false -> true
         *
         */
        val result = lastTrainTime.firstOrNull {
            enableDestinationStation.contains(it.destinationStationName).xor(suffix).not()
                    || transportIdRequest.destinationStation.name == it.destinationStationName
        }?.leftTime ?: throw IllegalArgumentException(LAST_TIME_LOGIC_ERROR)

        return result
    }

    private fun checkInnerOrOuter(
        stationType: Int,
        startStationIndex: Int,
        endStationIndex: Int,
        stationsOfLine: List<Station>,
    ): SubwayCircleType {
        return if (stationType == 2) {
            if (startStationIndex < endStationIndex) {
                if (stationsOfLine[startStationIndex].frCode.contains("211-") // 성수 ~ 신설동 예외처리
                    || stationsOfLine[endStationIndex].frCode.contains("211-")
                ) {
                    SubwayCircleType.OUTER
                } else {
                    SubwayCircleType.INNER
                }
            } else {
                if (startStationIndex - endStationIndex >= EMPIRICAL_DISTINCTION) {
                    SubwayCircleType.INNER
                } else {
                    SubwayCircleType.OUTER
                }
            }
        } else {
            if (startStationIndex < endStationIndex) {
                SubwayCircleType.OUTER
            } else {
                SubwayCircleType.INNER
            }
        }
    }

    private fun checkStationCase(
        stationType: Int,
        subwayCircleType: SubwayCircleType,
        startIndex: Int,
        endIndex: Int,
        stationsOfLine: List<Station>,
    ): Boolean {
        if (stationType != 2) {
            return false
        }

        if (subwayCircleType == SubwayCircleType.OUTER) {
            if (startIndex < endIndex) {
                if (stationsOfLine[startIndex].frCode.contains("211-") // 성수 ~ 신설동 예외처리
                    || stationsOfLine[endIndex].frCode.contains("211-")
                ) {
                    return false
                }
                return true
            }
            return false
        }
        return startIndex > endIndex
    }

    /**
     * 요일 별로 막차 시간이 다르기 때문에, 앱을 실행하는 오늘의 요일도 받아야 한다.
     */
    private fun whaWeekToday(): WeekType {
        return WeekType.WEEK
    }

    private suspend fun getBusLastTransportTime(transportIdRequest: TransportIdRequest): String {
        when (transportIdRequest.area) {
            Area.GYEONGGI -> {
                val lastTime = routeRepository.getGyeonggiBusLastTime(
                    transportIdRequest.lineId
                ).msgBody.routeList

                // 해당 노선의 정류소 목록을 가져와서 기점, 종점 구분하기
                val stations =
                    routeRepository.getGyeonggiBusRouteStations(transportIdRequest.lineId)
                        .msgBody.stations

                var startIndex: Int = -1
                var endIndex: Int = -1

                for ((index, station) in stations.withIndex()) {
                    val stationId = station.stationId.toString()

                    if (stationId == transportIdRequest.stationId) {
                        startIndex = index
                    } else if (stationId == transportIdRequest.destinationStationId) {
                        endIndex = index
                    }

                    if (startIndex != -1 && endIndex != -1) {
                        break
                    }
                }

                if (startIndex == -1 || endIndex == -1) {
                    throw NoAppropriateDataException(NO_BUS_ARS_ID)
                }

                return if (startIndex < endIndex) {
                    lastTime.first().upLastTime + ":00"
                } else {
                    lastTime.first().downLastTime + ":00"
                }
            }
            Area.SEOUL -> {
                var lastTime = routeRepository.getSeoulBusLastTime(
                    transportIdRequest.stationId,
                    transportIdRequest.lineId
                )?.toInt() ?: throw ApiServerDataException(API_SERVER_DATA_ERROR)

                if (lastTime < MID_NIGHT) {
                    lastTime += MID_NIGHT
                }
                return lastTime.toString().chunked(2).joinToString(":")
            }
            Area.UN_SUPPORT_AREA -> {
                TODO()
            }
        }
    }


    private suspend fun getSeoulBusLineId(transportIdRequest: TransportIdRequest): TransportIdRequest {
        val busName = transportIdRequest.lineName.split(":")[1]
        val line =
            routeRepository.getSeoulBusLine(transportIdRequest.stationId).lineIdMsgBody.busLines.firstOrNull {
                it.busLineName.contains(busName)
            } ?: throw NoAppropriateDataException(NO_BUS_LINE_ID)

        return transportIdRequest.changeLineId(line.lineId, line.term)
    }

    private suspend fun getStationId(transportIdRequest: TransportIdRequest): TransportIdRequest {
        return when (transportIdRequest.transportMoveType) {
            TransportMoveType.SUBWAY -> {
                if (allowedSubwayLineForUse.contains(transportIdRequest.stationType).not()) {
                    throw NoAppropriateDataException(NOT_REGISTERED_STATION)
                }
                transportIdRequest.changeStartStationId(
                    routeRepository.getSubwayStationCd(
                        transportIdRequest.stationId,
                        transportIdRequest.stationName
                    )
                )
            }
            TransportMoveType.BUS -> getBusIdUsedAtPublicApi(transportIdRequest)
        }
    }

    private suspend fun getArea(coordinate: Coordinate): Area {
        val areaName =
            routeRepository.reverseGeocoding(coordinate, AddressType.LOT_ADDRESS).addressInfo.cityDo

        return Area.getAreaByName(areaName)
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
    ): TransportIdRequest {
        when (transportIdRequest.area) {
            Area.GYEONGGI -> {
                if (transportIdRequest.lineName.contains("마을")) {
                    throw NoAppropriateDataException(GYEONGGI_REGION_BUS_NOT_SUPPORT)
                }

                val startStationId = getGyeonggiBusStationId(
                    Place(
                        transportIdRequest.stationName,
                        transportIdRequest.coordinate
                    )
                )

                val endStationId = getGyeonggiBusStationId(
                    Place(
                        transportIdRequest.destinationStation.name,
                        transportIdRequest.destinationStation.coordinate
                    )
                )

                if (startStationId == UNKNOWN_ID || endStationId == UNKNOWN_ID) {
                    throw NoAppropriateDataException(NO_BUS_ARS_ID)
                }

                return transportIdRequest.changeStartStationId(startStationId)
                    .changeDestinationStationId(endStationId)
            }
            Area.SEOUL -> {
                val busStations =
                    routeRepository.getSeoulBusStationArsId(transportIdRequest.stationName)
                        .arsIdMsgBody
                        .busStations

                val arsId = findClosestSeoulBusStation(transportIdRequest, busStations)

                if (arsId == UNKNOWN_ID) {
                    throw NoAppropriateDataException(NO_BUS_ARS_ID)
                }
                return transportIdRequest.changeStartStationId(arsId)
            }
            Area.UN_SUPPORT_AREA -> throw NoAppropriateDataException(UN_SUPPORT_AREA)
        }
    }

    private suspend fun getGyeonggiBusStationId(
        place: Place,
    ): String {
        val busStations =
            routeRepository.getGyeonggiBusStationId(place.name).msgBody.busStations

        return findClosestGyeonggiBusStation(place, busStations)
    }

    private fun findClosestSeoulBusStation(
        transportIdRequest: TransportIdRequest,
        busStations: List<BusStationInfo>,
    ): String {
        val originLongitude = correctLongitudeValue(transportIdRequest.coordinate.longitude)
        val originLatitude = correctLatitudeValue(transportIdRequest.coordinate.latitude)
        var closestStation: BusStationInfo? = null
        var closestDistance = 0.0

        busStations.filter {
            it.stationName == transportIdRequest.stationName
        }.map {
            if (closestStation == null) {
                closestStation = it
                val x = abs(originLongitude - correctLongitudeValue(it.longitude)).toDouble()
                val y = abs(originLatitude - correctLatitudeValue(it.latitude)).toDouble()
                closestDistance = x * x + y * y
                return@map
            }

            val x = abs(originLongitude - correctLongitudeValue(it.longitude)).toDouble()
            val y = abs(originLatitude - correctLatitudeValue(it.latitude)).toDouble()
            val distance = x * x + y * y

            if (distance < closestDistance) {
                closestDistance = distance
                closestStation = it
            }
        }

        return closestStation?.arsId ?: UNKNOWN_ID
    }

    private fun findClosestGyeonggiBusStation(
        place: Place,
        busStations: List<GyeonggiBusStation>,
    ): String {
        val originLongitude = correctLongitudeValue(place.coordinate.longitude)
        val originLatitude = correctLatitudeValue(place.coordinate.latitude)
        var closestStation: GyeonggiBusStation? = null
        var closestDistance = 0

        busStations.filter {
            it.stationName == place.name
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

        return closestStation?.stationId?.toString() ?: UNKNOWN_ID
    }

    private fun correctLongitudeValue(longitude: String): Int {
        return ((longitude.toDouble() - KOREA_LONGITUDE) * CORRECTION_VALUE).toInt()
    }

    private fun correctLatitudeValue(latitude: String): Int {
        return ((latitude.toDouble() - KOREA_LATITUDE) * CORRECTION_VALUE).toInt()
    }

    companion object {
        // UI에 노출되지 않고, 개발자를 위한 디버깅 목적의 스트링
        private const val NOT_REGISTERED_STATION = "API를 지원하지 않는 전철역입니다."
        private const val NO_BUS_ARS_ID = "버스 정류소 고유 아이디가 없습니다."
        private const val NO_BUS_LINE_ID = "버스 노선 고유 아이디가 없습니다."
        private const val NO_SUBWAY_STATION = "노선에 해당하는 지하철이 없습니다."
        private const val UN_SUPPORT_AREA = "지원하지 않는 지역입니다."
        private const val LAST_TIME_LOGIC_ERROR = "막차 시간 로직이 잘못되었습니다."
        private const val GYEONGGI_REGION_BUS_NOT_SUPPORT = "경기도 마을 버스 정보는 API에서 제공하지 않습니다."
        private const val API_SERVER_DATA_ERROR = "로직이 올바르지만 서버에 데이터가 없습니다."

        private const val UNKNOWN_ID = "0"
        private const val NOT_YET_CALCULATED = 0
        private const val KOREA_LONGITUDE = 127
        private const val KOREA_LATITUDE = 37
        private const val CORRECTION_VALUE = 100_000

        private const val EMPIRICAL_DISTINCTION = 20

        private const val SUBWAY_LINE_ONE = 1
        private const val SUBWAY_LINE_EIGHT = 8

        private const val MID_NIGHT = 60_000
    }
}