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
    class NoServiceAreaException : Exception("지원하지 않는 지역입니다.")
    class ApiServerDataException : Exception("로직이 올바르지만 서버에 데이터가 없습니다.")

    private val allowedSubwayLineForUse = (SUBWAY_LINE_ONE..SUBWAY_LINE_EIGHT)

    override suspend fun invoke(itinerary: Itinerary): TransportLastTimeInfo {
        var transportIdRequests: List<TransportIdRequest?> = createTransportIdRequests(itinerary)
        transportIdRequests = convertStationId(transportIdRequests)
        transportIdRequests = convertRouteId(transportIdRequests)

        val dataWithLastTime: List<String?> = getLastTransportTime(transportIdRequests)

        // 막차 시간 중 가장 빠른 시간과 dataWithLastTime을 가지는 데이터 클래스 반환하기
        return TransportLastTimeInfo(dataWithLastTime.sortedBy { it }.first() ?: "")
    }

    private suspend fun getLastTransportTime(transportIdRequests: List<TransportIdRequest?>): List<String?> {
        return transportIdRequests.map { transportIdRequest ->
            if (transportIdRequest == null) {
                return@map null
            }
            try {
                when (transportIdRequest.transportMoveType) {
                    TransportMoveType.BUS -> getBusLastTransportTime(transportIdRequest)
                    TransportMoveType.SUBWAY -> getSubwayLastTransportTime(transportIdRequest)
                }
            } catch (exception: ApiServerDataException) {
                null
            } catch (exception: NoAppropriateDataException) {
                null
            } catch (exception: NoServiceAreaException) {
                null
            }
        }
    }

    // 공공데이터 포털에서 사용하는 버스 노선 번호를 계산하는 작업
    // 지하철은 별도의 작업이 필요하지 않습니다.
    private suspend fun convertRouteId(
        transportIdRequests: List<TransportIdRequest?>
    ): List<TransportIdRequest?> {
        return transportIdRequests.map { transportIdRequest ->
            if (transportIdRequest == null) {
                return@map null
            }
            try {
                when (transportIdRequest.transportMoveType) {
                    TransportMoveType.BUS -> {
                        when (transportIdRequest.area) {
                            Area.SEOUL -> convertSeoulBusRouteId(transportIdRequest)
                            Area.GYEONGGI -> convertGyeonggiBusRouteId(transportIdRequest)
                            Area.UN_SUPPORT_AREA -> throw NoServiceAreaException()
                        }
                    }
                    TransportMoveType.SUBWAY -> transportIdRequest
                }
            } catch (exception: NoAppropriateDataException) {
                null
            } catch (exception: NoServiceAreaException) {
                null
            }
        }
    }

    // 공공데이터 포털에서 사용하는 버스 정류소, 지하철 역의 고유번호로 변환하는 작업
    private suspend fun convertStationId(
        transportIdRequests: List<TransportIdRequest?>
    ): List<TransportIdRequest?> {
        return transportIdRequests.map { transportIdRequest ->
            if (transportIdRequest == null) {
                return@map null
            }
            try {
                when (transportIdRequest.transportMoveType) {
                    TransportMoveType.SUBWAY -> convertSubwayStationId(transportIdRequest)
                    TransportMoveType.BUS -> convertBusStationId(transportIdRequest)
                }
            } catch (exception: NoAppropriateDataException) {
                null
            } catch (exception: ApiServerDataException) {
                null
            } catch (exception: NoServiceAreaException) {
                null
            }
        }
    }

    private suspend fun convertSubwayStationId(
        transportIdRequest: TransportIdRequest
    ): TransportIdRequest {
        // 22년 11월 기준, 공공데이터 포털에서 1 ~ 8호선에 속한 지하철 역의 막차 시간만 제공합니다.
        if (allowedSubwayLineForUse.contains(transportIdRequest.stationType).not()) {
            throw NoAppropriateDataException(NOT_REGISTERED_STATION)
        }
        return transportIdRequest.changeStartStationId(
            routeRepository.getSubwayStationCd(
                transportIdRequest.stationId,
                transportIdRequest.stationName
            )
        )
    }

    // 승차지, 도착지, 고유 번호를 알아내는데 필요한 정보로만 구성된 데이터 클래스로 변환하기
    private suspend fun createTransportIdRequests(itinerary: Itinerary): List<TransportIdRequest> {
        return itinerary.routes.fold(listOf<TransportIdRequest>()) { transportIdRequests, route ->
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
                        routeName = route.routeInfo,
                        routeId = UNKNOWN_ID,
                        term = NOT_YET_CALCULATED,
                        destinationStation = route.end,
                        destinationStationId = UNKNOWN_ID,
                    )
                }
                else -> transportIdRequests
            }
        }
    }

    private suspend fun convertGyeonggiBusRouteId(
        transportIdRequest: TransportIdRequest
    ): TransportIdRequest {
        val busName = transportIdRequest.routeName.split(":")[1]
        val route = routeRepository.getGyeonggiBusRoute(transportIdRequest.stationId)
            .msgBody.routeList.firstOrNull {
                it.busName.contains(busName)
            } ?: throw NoAppropriateDataException(NO_BUS_ROUTE_ID)

        return transportIdRequest.changeRouteId(route.routeId, null)
    }

    // TODO: 외선, 내선 로직 정리하면서 함수 분리하기
    private suspend fun getSubwayLastTransportTime(transportIdRequest: TransportIdRequest): String {
        val stationsOfLine =
            routeRepository.getSubwayStations(transportIdRequest.stationType.toString())
                .sortedWith(compareBy { it.frCode })

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
        return when (transportIdRequest.area) {
            Area.GYEONGGI -> getGyeonggiBusLastTransportTime(transportIdRequest)
            Area.SEOUL -> getSeoulBusLastTransportTime(transportIdRequest)
            Area.UN_SUPPORT_AREA -> throw NoServiceAreaException()
        }
    }

    private suspend fun getSeoulBusLastTransportTime(
        transportIdRequest: TransportIdRequest
    ): String {
        var lastTime = routeRepository.getSeoulBusLastTime(
            transportIdRequest.stationId,
            transportIdRequest.routeId
        )?.toInt() ?: throw ApiServerDataException()

        if (lastTime < MID_NIGHT) {
            lastTime += TIME_CORRECTION_VALUE
        }
        return lastTime.toString().chunked(2).joinToString(":")
    }

    private suspend fun getGyeonggiBusLastTransportTime(
        transportIdRequest: TransportIdRequest
    ): String {
        val lastTime = routeRepository.getGyeonggiBusLastTime(
            transportIdRequest.routeId
        ).msgBody.routeList.first()

        val destinationIsLastStation = checkGyeonggiBusDestinationIsLastStation(transportIdRequest)

        return if (destinationIsLastStation) {
            addSecondsFormat(lastTime.upLastTime)
        } else {
            addSecondsFormat(lastTime.downLastTime)
        }
    }

    private fun addSecondsFormat(time: String): String {
        return "$time:00"
    }

    // 해당 노선의 정류소 목록을 가져와서 버스의 기점행, 종점행 구분
    private suspend fun checkGyeonggiBusDestinationIsLastStation(
        transportIdRequest: TransportIdRequest
    ): Boolean {
        val stations =
            routeRepository.getGyeonggiBusRouteStations(transportIdRequest.routeId)
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

        return startIndex < endIndex
    }

    private suspend fun convertSeoulBusRouteId(
        transportIdRequest: TransportIdRequest
    ): TransportIdRequest {
        val busName = transportIdRequest.routeName.split(":")[1]
        val route = routeRepository.getSeoulBusRoute(transportIdRequest.stationId)
            .routeIdMsgBody.busRoutes.firstOrNull {
                it.busRouteName.contains(busName)
            } ?: throw NoAppropriateDataException(NO_BUS_ROUTE_ID)

        return transportIdRequest.changeRouteId(route.routeId, route.term)
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
    private suspend fun convertBusStationId(
        transportIdRequest: TransportIdRequest
    ): TransportIdRequest {
        return when (transportIdRequest.area) {
            Area.GYEONGGI -> convertGyeonggiBusStationId(transportIdRequest)
            Area.SEOUL -> convertSeoulBusStationId(transportIdRequest)
            Area.UN_SUPPORT_AREA -> throw NoServiceAreaException()
        }
    }

    private suspend fun convertSeoulBusStationId(
        transportIdRequest: TransportIdRequest
    ): TransportIdRequest {
        val busStations =
            routeRepository.getSeoulBusStationArsId(transportIdRequest.stationName)
                .arsIdMsgBody
                .busStations

        val arsId = findClosestSeoulBusStation(transportIdRequest, busStations)

        // API 서버 데이터의 문제로 버스 정류소 고유 아이디가 없는 경우가 있습니다.
        if (arsId == UNKNOWN_ID) {
            throw ApiServerDataException()
        }
        return transportIdRequest.changeStartStationId(arsId)
    }

    private suspend fun convertGyeonggiBusStationId(
        transportIdRequest: TransportIdRequest
    ): TransportIdRequest {
        if (transportIdRequest.routeName.contains(LOCAL_BUS_NAME)) {
            throw NoAppropriateDataException(GYEONGGI_REGION_BUS_NOT_SUPPORT)
        }

        val startStationId = getGyeonggiBusStationId(
            Place(transportIdRequest.stationName, transportIdRequest.coordinate)
        )

        // API 서버 데이터의 문제로 버스 정류소 고유 아이디가 없는 경우가 있습니다.
        if (startStationId == UNKNOWN_ID) {
            throw ApiServerDataException()
        }

        val endStationId = getGyeonggiBusStationId(
            Place(
                transportIdRequest.destinationStation.name,
                transportIdRequest.destinationStation.coordinate
            )
        )

        // API 서버 데이터의 문제로 버스 정류소 고유 아이디가 없는 경우가 있습니다.
        if (endStationId == UNKNOWN_ID) {
            throw NoAppropriateDataException(NO_BUS_ARS_ID)
        }

        return transportIdRequest.changeStartStationId(startStationId)
            .changeDestinationStationId(endStationId)
    }

    private suspend fun getGyeonggiBusStationId(place: Place): String {
        val busStations = routeRepository.getGyeonggiBusStationId(place.name).msgBody.busStations

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
        private const val NO_BUS_ROUTE_ID = "버스 노선 고유 아이디가 없습니다."
        private const val NO_SUBWAY_STATION = "노선에 해당하는 지하철이 없습니다."
        private const val LAST_TIME_LOGIC_ERROR = "막차 시간 로직이 잘못되었습니다."
        private const val GYEONGGI_REGION_BUS_NOT_SUPPORT = "경기도 마을 버스 정보는 API에서 제공하지 않습니다."

        private const val LOCAL_BUS_NAME = "마을"
        private const val UNKNOWN_ID = "0"
        private const val NOT_YET_CALCULATED = 0
        private const val KOREA_LONGITUDE = 127
        private const val KOREA_LATITUDE = 37
        private const val CORRECTION_VALUE = 100_000

        private const val EMPIRICAL_DISTINCTION = 20

        private const val SUBWAY_LINE_ONE = 1
        private const val SUBWAY_LINE_EIGHT = 8

        private const val MID_NIGHT = 60_000
        private const val TIME_CORRECTION_VALUE = 240_000
    }
}