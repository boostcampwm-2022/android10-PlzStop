package com.stop.domain.usecase.route

import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.route.*
import com.stop.domain.model.route.gyeonggi.GyeonggiBusStation
import com.stop.domain.model.route.seoul.bus.BusStationInfo
import com.stop.domain.model.route.seoul.subway.Station
import com.stop.domain.model.route.seoul.subway.TransportDirectionType
import com.stop.domain.model.route.seoul.subway.WeekType
import com.stop.domain.model.route.tmap.custom.*
import com.stop.domain.repository.RouteRepository
import javax.inject.Inject
import kotlin.math.abs

internal class GetLastTransportTimeUseCaseImpl @Inject constructor(
    private val routeRepository: RouteRepository
) : GetLastTransportTimeUseCase {

    private val allowedSubwayLineForUse = (SUBWAY_LINE_ONE..SUBWAY_LINE_EIGHT)

    override suspend operator fun invoke(itinerary: Itinerary): List<TransportLastTime?> {
        var transportIdRequests: List<TransportIdRequest?> = createTransportIdRequests(itinerary)
        transportIdRequests = convertStationId(transportIdRequests)
        transportIdRequests = convertRouteId(transportIdRequests)

        return getLastTransportTime(transportIdRequests)
    }

    private suspend fun getLastTransportTime(transportIdRequests: List<TransportIdRequest?>): List<TransportLastTime?> {
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
            } catch (exception: IllegalArgumentException) {
                null
            }
        }
    }

    // ??????????????? ???????????? ???????????? ?????? ?????? ????????? ???????????? ??????
    // ???????????? ????????? ????????? ???????????? ????????????.
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

    // ??????????????? ???????????? ???????????? ?????? ?????????, ????????? ?????? ??????????????? ???????????? ??????
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
        // 22??? 11??? ??????, ??????????????? ???????????? 1 ~ 8????????? ?????? ????????? ?????? ?????? ????????? ???????????????.
        if (allowedSubwayLineForUse.contains(transportIdRequest.stationType).not()) {
            throw NoAppropriateDataException("API??? ???????????? ?????? ??????????????????.")
        }
        val stationCd = routeRepository.getSubwayStationCd(
            transportIdRequest.stationId,
            transportIdRequest.stationName
        )

        if (stationCd.isEmpty()) {
            throw ApiServerDataException()
        }

        return transportIdRequest.changeStartStationId(stationCd)
    }

    // ?????????, ?????????, ?????? ????????? ??????????????? ????????? ???????????? ????????? ????????? ???????????? ????????????
    private suspend fun createTransportIdRequests(itinerary: Itinerary): List<TransportIdRequest?> {
        var cumulativeSectionTime = 0

        return itinerary.routes.fold(listOf()) { transportIdRequests, route ->
            when (route) {
                is WalkRoute -> transportIdRequests + null
                is TransportRoute -> {
                    val startStation = route.stations.first()
                    val transportMoveType = TransportMoveType.getMoveTypeByName(route.mode.name)
                        ?: return@fold transportIdRequests

                    val sectionTime = route.sectionTime.toInt()
                    cumulativeSectionTime += sectionTime

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
                        sectionTime = sectionTime,
                        cumulativeSectionTime = cumulativeSectionTime,
                    )
                }
                else -> transportIdRequests + null
            }
        }
    }

    private suspend fun convertGyeonggiBusRouteId(
        transportIdRequest: TransportIdRequest
    ): TransportIdRequest {
        val busName = transportIdRequest.routeName.split(":")[1]
        val routes = routeRepository.getGyeonggiBusRoute(transportIdRequest.stationId)

        if (routes.isEmpty()) {
            throw ApiServerDataException()
        }
        val route = routes.firstOrNull {
            it.busName.contains(busName)
        } ?: throw NoAppropriateDataException("?????? ?????? ?????? ???????????? ????????????.")

        return transportIdRequest.changeRouteId(route.routeId, null)
    }

    private suspend fun getSubwayLastTransportTime(transportIdRequest: TransportIdRequest): TransportLastTime {
        val stationsOfLine =
            routeRepository.getSubwayStations(transportIdRequest.stationType.toString())
                .sortedWith(compareBy { it.frCode })

        if (stationsOfLine.isEmpty()) {
            throw ApiServerDataException()
        }

        val startStationIndex = stationsOfLine.indexOfFirst {
            it.stationName == transportIdRequest.stationName
        }
        if (startStationIndex == -1) {
            throw NoAppropriateDataException("????????? ???????????? ???????????? ????????????.")
        }

        val endStationIndex = stationsOfLine.indexOfFirst {
            it.stationName == transportIdRequest.destinationStation.name
        }
        if (endStationIndex == -1) {
            throw NoAppropriateDataException("????????? ???????????? ???????????? ????????????.")
        }

        // ??????, ?????? ?????? ??????
        // 2????????? FR_CODE??? ???????????? ??????, ??? ??? 1, 3 ~ 7????????? FR_CODE??? ???????????? ??????
        val subwayCircleType = checkInnerOrOuter(
            transportIdRequest.stationType,
            startStationIndex,
            endStationIndex,
            stationsOfLine
        )

        val weekType = getDayOfWeek()

        val stationsUntilStart: List<Station>
        val enableDestinationStation: List<Station>

        if (startStationIndex < endStationIndex) {
            stationsUntilStart = stationsOfLine.subList(0, startStationIndex + 1)
            enableDestinationStation = stationsOfLine.subList(startStationIndex, endStationIndex)
        } else {
            stationsUntilStart = stationsOfLine.subList(startStationIndex, stationsOfLine.size)
            enableDestinationStation =
                stationsOfLine.subList(endStationIndex + 1, startStationIndex + 1)
        }

        val lastTrainTime = routeRepository.getSubwayStationLastTime(
            transportIdRequest.stationId,
            subwayCircleType,
            weekType
        )

        if (lastTrainTime.isEmpty()) {
            throw ApiServerDataException()
        }

        val correctionValueByStationCase = checkStationCase(
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
        val result = lastTrainTime.firstOrNull { stationsListTime ->
            enableDestinationStation.any {
                it.stationName == stationsListTime.destinationStationName
            }.xor(correctionValueByStationCase)
                .not() || transportIdRequest.destinationStation.name == stationsListTime.destinationStationName
        }?.leftTime ?: throw IllegalArgumentException("?????? ?????? ????????? ?????????????????????.")

        return TransportLastTime(
            transportMoveType = transportIdRequest.transportMoveType,
            area = transportIdRequest.area,
            lastTime = result,
            timeToBoard = subtractSectionTimeFromLastTime(
                transportIdRequest.cumulativeSectionTime,
                result
            ),
            destinationStationName = transportIdRequest.destinationStation.name,
            stationsUntilStart = stationsUntilStart.map {
                TransportStation(
                    stationName = it.stationName,
                    stationId = it.stationCd,
                )
            },
            enableDestinationStation = enableDestinationStation.map {
                TransportStation(
                    stationName = it.stationName,
                    stationId = it.stationCd,
                )
            },
            transportDirectionType = subwayCircleType,
            routeId = transportIdRequest.routeId,
        )
    }

    private fun subtractSectionTimeFromLastTime(sectionTime: Int, lastTime: String): String {
        val (hour, minute, second) = lastTime.split(":").map { it.toInt() }
        val lastTimeSecond = hour * 60 * 60 + minute * 60 + second

        val realLastTimeSecond = lastTimeSecond - sectionTime

        val realHour = realLastTimeSecond / 60 / 60
        val realMinute = ((realLastTimeSecond / 60) % 60).toString().padStart(TIME_DIGIT, '0')
        val realSeconds = (realLastTimeSecond % 60).toString().padStart(TIME_DIGIT, '0')

        return "$realHour:$realMinute:$realSeconds"
    }

    private fun checkInnerOrOuter(
        stationType: Int,
        startStationIndex: Int,
        endStationIndex: Int,
        stationsOfLine: List<Station>,
    ): TransportDirectionType {
        return if (stationType == 2) {
            if (startStationIndex < endStationIndex) {
                if (stationsOfLine[startStationIndex].frCode.contains("211-") // ?????? ~ ????????? ????????????
                    || stationsOfLine[endStationIndex].frCode.contains("211-")
                ) {
                    TransportDirectionType.OUTER
                } else {
                    TransportDirectionType.INNER
                }
            } else {
                if (startStationIndex - endStationIndex >= EMPIRICAL_DISTINCTION) {
                    TransportDirectionType.INNER
                } else {
                    TransportDirectionType.OUTER
                }
            }
        } else {
            if (startStationIndex < endStationIndex) {
                TransportDirectionType.TO_FIRST
            } else {
                TransportDirectionType.TO_END
            }
        }
    }

    private fun checkStationCase(
        stationType: Int,
        transportDirectionType: TransportDirectionType,
        startIndex: Int,
        endIndex: Int,
        stationsOfLine: List<Station>,
    ): Boolean {
        if (stationType != 2) {
            return false
        }

        if (transportDirectionType == TransportDirectionType.OUTER) {
            if (startIndex < endIndex) {
                if (stationsOfLine[startIndex].frCode.contains("211-") // ?????? ~ ????????? ????????????
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
     * ?????? ?????? ?????? ????????? ????????? ?????????, ?????? ???????????? ????????? ????????? ????????? ??????.
     */
    private fun getDayOfWeek(): WeekType {
        return WeekType.WEEK
    }

    private suspend fun getBusLastTransportTime(transportIdRequest: TransportIdRequest): TransportLastTime {
        return when (transportIdRequest.area) {
            Area.GYEONGGI -> getGyeonggiBusLastTransportTime(transportIdRequest)
            Area.SEOUL -> getSeoulBusLastTransportTime(transportIdRequest)
            Area.UN_SUPPORT_AREA -> throw NoServiceAreaException()
        }
    }

    private suspend fun getSeoulBusLastTransportTime(
        transportIdRequest: TransportIdRequest
    ): TransportLastTime {
        val lastTimes = routeRepository.getSeoulBusLastTime(
            transportIdRequest.stationId,
            transportIdRequest.routeId
        )
        if (lastTimes.isEmpty()) {
            return getRectifiedGyeonggiBusLastTransportTime(transportIdRequest)
        }

        var lastTime = lastTimes.first().lastTime?.toInt() ?: throw ApiServerDataException()

        if (lastTime < MID_NIGHT) {
            lastTime += TIME_CORRECTION_VALUE
        }

        val lastTimeString = lastTime.toString().padStart(6, '0').chunked(2).joinToString(":")

        return TransportLastTime(
            transportMoveType = TransportMoveType.BUS,
            area = Area.SEOUL,
            lastTime = lastTimeString,
            timeToBoard = subtractSectionTimeFromLastTime(
                transportIdRequest.cumulativeSectionTime,
                lastTimeString
            ),
            destinationStationName = transportIdRequest.destinationStation.name,
            stationsUntilStart = listOf(),
            enableDestinationStation = listOf(),
            transportDirectionType = TransportDirectionType.UNKNOWN, // TODO: ?????? ????????? ??????, ?????? ??????
            routeId = transportIdRequest.routeId,
        )
    }

    private suspend fun getRectifiedGyeonggiBusLastTransportTime(
        transportIdRequest: TransportIdRequest
    ): TransportLastTime {
        var newTransportIdRequest = convertGyeonggiBusStationId(transportIdRequest)
        newTransportIdRequest = convertGyeonggiBusRouteId(newTransportIdRequest)

        val lastTime = routeRepository.getGyeonggiBusLastTime(
            newTransportIdRequest.routeId
        ).firstOrNull() ?: throw ApiServerDataException()

        val stations = getGyeonggiBusStationsAtRoute(newTransportIdRequest)
        val (directionType, startIndex) = checkGyeonggiBusDirection(
            stations,
            newTransportIdRequest
        )

        val stationsUntilStart: List<TransportStation>
        val time: String

        if (directionType == TransportDirectionType.INNER) {
            stationsUntilStart = stations.subList(0, startIndex + 1).map {
                TransportStation(
                    stationName = it.stationName,
                    stationId = it.stationId.toString(),
                )
            }
            time = addSecondsFormat(lastTime.upLastTime)
        } else {
            stationsUntilStart = stations.subList(startIndex, stations.size).reversed().map {
                TransportStation(
                    stationName = it.stationName,
                    stationId = it.stationId.toString(),
                )
            }
            time = addSecondsFormat(lastTime.downLastTime)
        }

        return TransportLastTime(
            transportMoveType = newTransportIdRequest.transportMoveType,
            area = newTransportIdRequest.area,
            transportDirectionType = directionType,
            lastTime = time,
            timeToBoard = subtractSectionTimeFromLastTime(
                newTransportIdRequest.cumulativeSectionTime,
                time
            ),
            destinationStationName = newTransportIdRequest.destinationStation.name,
            stationsUntilStart = stationsUntilStart,
            enableDestinationStation = listOf(),
            routeId = newTransportIdRequest.routeId,
        )
    }

    private suspend fun getGyeonggiBusLastTransportTime(
        transportIdRequest: TransportIdRequest
    ): TransportLastTime {
        val lastTime = routeRepository.getGyeonggiBusLastTime(
            transportIdRequest.routeId
        ).firstOrNull() ?: return getRectifiedSeoulBusLastTransportTime(transportIdRequest)


        val stations = getGyeonggiBusStationsAtRoute(transportIdRequest)
        val (directionType, startIndex) = checkGyeonggiBusDirection(
            stations,
            transportIdRequest
        )

        val stationsUntilStart: List<TransportStation>
        val time: String

        if (directionType == TransportDirectionType.INNER) {
            stationsUntilStart = stations.subList(0, startIndex + 1).map {
                TransportStation(
                    stationName = it.stationName,
                    stationId = it.stationId.toString(),
                )
            }
            time = addSecondsFormat(lastTime.upLastTime)
        } else {
            stationsUntilStart = stations.subList(startIndex, stations.size).reversed().map {
                TransportStation(
                    stationName = it.stationName,
                    stationId = it.stationId.toString(),
                )
            }
            time = addSecondsFormat(lastTime.downLastTime)
        }

        return TransportLastTime(
            transportMoveType = transportIdRequest.transportMoveType,
            area = transportIdRequest.area,
            transportDirectionType = directionType,
            lastTime = time,
            timeToBoard = subtractSectionTimeFromLastTime(
                transportIdRequest.cumulativeSectionTime,
                time
            ),
            destinationStationName = transportIdRequest.destinationStation.name,
            stationsUntilStart = stationsUntilStart,
            enableDestinationStation = listOf(),
            routeId = transportIdRequest.routeId,
        )
    }

    private suspend fun getRectifiedSeoulBusLastTransportTime(
        transportIdRequest: TransportIdRequest
    ): TransportLastTime {
        var newTransportIdRequest = convertSeoulBusStationId(transportIdRequest)
        newTransportIdRequest = convertSeoulBusRouteId(newTransportIdRequest)

        val lastTimes = routeRepository.getSeoulBusLastTime(
            newTransportIdRequest.stationId,
            newTransportIdRequest.routeId
        )

        if (lastTimes.isEmpty()) {
            throw ApiServerDataException()
        }

        var lastTime = lastTimes.first().lastTime?.toInt() ?: throw ApiServerDataException()

        if (lastTime < MID_NIGHT) {
            lastTime += TIME_CORRECTION_VALUE
        }

        val lastTimeString = lastTime.toString().padStart(6, '0').chunked(2).joinToString(":")

        return TransportLastTime(
            transportMoveType = TransportMoveType.BUS,
            area = Area.SEOUL,
            lastTime = lastTimeString,
            timeToBoard = subtractSectionTimeFromLastTime(
                newTransportIdRequest.cumulativeSectionTime,
                lastTimeString
            ),
            destinationStationName = transportIdRequest.destinationStation.name,
            stationsUntilStart = listOf(),
            enableDestinationStation = listOf(),
            transportDirectionType = TransportDirectionType.UNKNOWN,
            routeId = transportIdRequest.routeId,
        )
    }

    private fun addSecondsFormat(time: String): String {
        return "$time:00"
    }

    private suspend fun getGyeonggiBusStationsAtRoute(
        transportIdRequest: TransportIdRequest
    ): List<GyeonggiBusStation> {
        val stations = routeRepository.getGyeonggiBusRouteStations(transportIdRequest.routeId)

        if (stations.isEmpty()) {
            throw ApiServerDataException()
        }
        return stations

    }

    // ?????? ????????? ????????? ????????? ???????????? ????????? ?????????, ????????? ??????
    private fun checkGyeonggiBusDirection(
        stations: List<GyeonggiBusStation>,
        transportIdRequest: TransportIdRequest
    ): Pair<TransportDirectionType, Int> {
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
            throw NoAppropriateDataException("?????? ????????? ?????? ???????????? ????????????.")
        }

        if (startIndex < endIndex) {
            return Pair(TransportDirectionType.TO_END, startIndex)
        }
        return Pair(TransportDirectionType.TO_FIRST, startIndex)
    }

    private suspend fun convertSeoulBusRouteId(
        transportIdRequest: TransportIdRequest
    ): TransportIdRequest {
        val busName = transportIdRequest.routeName.split(":")[1]
        val busRouteInfo = routeRepository.getSeoulBusRoute(transportIdRequest.stationId)

        if (busRouteInfo.isEmpty()) {
            throw ApiServerDataException()
        }

        val route = busRouteInfo.firstOrNull {
            it.busRouteName.contains(busName)
        } ?: throw NoAppropriateDataException("?????? ?????? ?????? ???????????? ????????????.")

        return transportIdRequest.changeRouteId(route.routeId, route.term)
    }

    private suspend fun getArea(coordinate: Coordinate): Area {
        val areaName = try {
            routeRepository.reverseGeocoding(coordinate, AddressType.LOT_ADDRESS).addressInfo.cityDo
        } catch (exception: IllegalArgumentException) {
            return Area.UN_SUPPORT_AREA
        }

        return Area.getAreaByName(areaName)
    }

    /**
     * ????????? ?????? ??????, ?????? ?????? ????????? ????????? ID??? ????????????.
     * T MAP??? ???????????? ????????? ????????? ???????????????, ?????????????????? ????????? ????????? ????????? ???????????? ?????????
     * ????????? ?????? ????????? ?????? ???????????? ?????????.
     * ?????? ?????? API??? ????????? ???????????? ???????????? arsId??? 0?????? ????????????.
     * arsID??? 0??? ?????? ?????? ????????? ???????????????, ????????? ????????? ???????????? ????????? ??? ????????? ??????????????????.
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

        if (busStations.isEmpty()) {
            throw ApiServerDataException()
        }

        val arsId = findClosestSeoulBusStation(transportIdRequest, busStations)

        // API ?????? ???????????? ????????? ?????? ????????? ?????? ???????????? ?????? ????????? ????????????.
        if (arsId == UNKNOWN_ID) {
            throw ApiServerDataException()
        }
        return transportIdRequest.changeStartStationId(arsId)
    }

    private suspend fun convertGyeonggiBusStationId(
        transportIdRequest: TransportIdRequest
    ): TransportIdRequest {
        if (transportIdRequest.routeName.contains(LOCAL_BUS_NAME)) {
            throw NoAppropriateDataException("????????? ?????? ?????? ????????? API?????? ???????????? ????????????.")
        }

        val startStationId = getGyeonggiBusStationId(
            Place(transportIdRequest.stationName, transportIdRequest.coordinate)
        )

        // API ?????? ???????????? ????????? ?????? ????????? ?????? ???????????? ?????? ????????? ????????????.
        if (startStationId == UNKNOWN_ID) {
            throw ApiServerDataException()
        }

        val endStationId = getGyeonggiBusStationId(
            Place(
                transportIdRequest.destinationStation.name,
                transportIdRequest.destinationStation.coordinate
            )
        )

        // API ?????? ???????????? ????????? ?????? ????????? ?????? ???????????? ?????? ????????? ????????????.
        if (endStationId == UNKNOWN_ID) {
            throw NoAppropriateDataException("?????? ????????? ?????? ???????????? ????????????.")
        }

        return transportIdRequest.changeStartStationId(startStationId)
            .changeDestinationStationId(endStationId)
    }

    private suspend fun getGyeonggiBusStationId(place: Place): String {
        val busStations = routeRepository.getGyeonggiBusStationId(place.name)

        if (busStations.isEmpty()) {
            throw ApiServerDataException()
        }
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
        private const val LOCAL_BUS_NAME = "??????"
        private const val UNKNOWN_ID = "0"
        private const val NOT_YET_CALCULATED = 0
        private const val KOREA_LONGITUDE = 127
        private const val KOREA_LATITUDE = 37
        private const val CORRECTION_VALUE = 100_000
        private const val TIME_DIGIT = 2

        private const val EMPIRICAL_DISTINCTION = 20

        private const val SUBWAY_LINE_ONE = 1
        private const val SUBWAY_LINE_EIGHT = 8

        private const val MID_NIGHT = 60_000
        private const val TIME_CORRECTION_VALUE = 240_000
    }
}

class NoAppropriateDataException(override val message: String) : Exception()
class NoServiceAreaException : Exception("???????????? ?????? ???????????????.")
class ApiServerDataException : Exception("????????? ??????????????? ????????? ???????????? ????????????.")