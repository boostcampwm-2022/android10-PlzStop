package com.stop.domain.usecase.route

import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.route.gyeonggi.GyeonggiBusStation
import com.stop.domain.model.route.seoul.bus.BusStationInfo
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.model.route.tmap.custom.*
import com.stop.domain.model.route.tmap.origin.Leg
import com.stop.domain.repository.RouteRepository
import javax.inject.Inject
import kotlin.math.abs

internal class GetRouteUseCaseImpl @Inject constructor(
    private val routeRepository: RouteRepository
) : GetRouteUseCase {

    private val allowedSubwayLineForUse = (SUBWAY_LINE_ONE..SUBWAY_LINE_EIGHT).toList()

    override suspend fun getRoute(routeRequest: RouteRequest): List<Itinerary> {
        val originRouteData = routeRepository.getRoute(routeRequest)

        return originRouteData.metaData.plan.itineraries.fold(listOf()) itinerary@{ itineraries, itinerary ->
            val result = itinerary.legs.fold(listOf<Route>()) { routes, leg ->
                try {
                    val moveType = MoveType.getMoveTypeByName(leg.mode)

                    routes + when (moveType) {
                        MoveType.SUBWAY, MoveType.BUS -> createPublicTransportRoute(leg, moveType)
                        MoveType.WALK -> createWalkRoute(leg, moveType)
                        else -> return@fold routes
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    return@itinerary itineraries
                }
            }
            itineraries + Itinerary(
                totalFare = itinerary.fare.regular.totalFare.toString(),
                routes = result,
                totalDistance = itinerary.totalDistance,
                totalTime = itinerary.totalTime,
                transferCount = itinerary.transferCount,
                walkTime = itinerary.walkTime,
            )
        }
    }


    private fun createPublicTransportRoute(leg: Leg, moveType: MoveType): TransportRoute {
        return TransportRoute(
            distance = leg.distance,
            end = with(leg.end) {
                Place(
                    name = name,
                    coordinate = Coordinate(
                        latitude = lat.toString(),
                        longitude = lon.toString()
                    )
                )
            },
            mode = moveType,
            sectionTime = leg.sectionTime,
            start = with(leg.start) {
                Place(
                    name = name,
                    coordinate = Coordinate(
                        latitude = lat.toString(),
                        longitude = lon.toString()
                    )
                )
            },
            lines = createCoordinates(leg.passShape?.linestring ?: ""),
            stations = leg.passStopList?.stationList?.map { station ->
                with(station) {
                    Station(
                        orderIndex = index,
                        coordinate = Coordinate(
                            latitude = lat,
                            longitude = lon
                        ),
                        stationId = stationID,
                        stationName = stationName,
                    )
                }
            } ?: listOf(),
            routeInfo = leg.route ?: "",
            routeColor = leg.routeColor ?: "",
            routeType = leg.type ?: -1,
        )
    }

    private suspend fun getIdUsedAtPublicApi(
        routeType: Int,
        station: com.stop.domain.model.route.tmap.origin.Station,
        moveType: MoveType,
    ): String {
        return when (moveType) {
            MoveType.SUBWAY -> {
                if (allowedSubwayLineForUse.contains(routeType).not()) {
                    throw IllegalArgumentException(NOT_REGISTERED_STATION)
                }
                routeRepository.getSubwayStationCd(station.stationID, station.stationName)
            }
            MoveType.BUS -> {
                if (station.stationName.contains("마을")) {
                    throw IllegalArgumentException(GYEONGGI_REGION_BUS_NOT_SUPPORT)
                }
                getBusIdUsedAtPublicApi(station)
            }
            else -> throw IllegalArgumentException(NO_SUPPORTING_TYPE)
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
        station: com.stop.domain.model.route.tmap.origin.Station
    ): String {
        val reverseGeocodingResponse =
            routeRepository.reverseGeocoding(
                Coordinate(station.lat, station.lon),
                AddressType.LOT_ADDRESS
            )
        val arsId = when (reverseGeocodingResponse.addressInfo.cityDo) {
            GYEONGGI_DO -> {
                val busStations =
                    routeRepository.getGyeonggiBusStationId(station.stationName).msgBody.busStations

                findClosestGyeonggiBusStation(station, busStations)
            }
            SEOUL -> {
                val busStations =
                    routeRepository.getSeoulBusStationArsId(station.stationName).msgBody.busStations

                findClosestSeoulBusStation(station, busStations)
            }
            else -> throw IllegalArgumentException(NO_SUPPORTING_CITY)
        }

        if (arsId == UNKNOWN_ARS_ID) {
            throw IllegalArgumentException(NO_BUS_ARS_ID)
        }
        return arsId
    }

    private fun findClosestSeoulBusStation(
        station: com.stop.domain.model.route.tmap.origin.Station,
        busStations: List<BusStationInfo>,
    ): String {
        val originLongitude = correctLongitudeValue(station.lon)
        val originLatitude = correctLatitudeValue(station.lat)
        var closestStation: BusStationInfo? = null
        var closestDistance = 0

        busStations.filter {
            it.stationName == station.stationName
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
        station: com.stop.domain.model.route.tmap.origin.Station,
        busStations: List<GyeonggiBusStation>,
    ): String {
        val originLongitude = correctLongitudeValue(station.lon)
        val originLatitude = correctLatitudeValue(station.lat)
        var closestStation: GyeonggiBusStation? = null
        var closestDistance = 0

        busStations.filter {
            it.stationName == station.stationName
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

    private fun createCoordinates(linesString: String): List<Coordinate> {
        return linesString.split(" ").map {
            val (latitude, longitude) = it.split(",")
            Coordinate(latitude, longitude)
        }
    }

    private fun createWalkRoute(leg: Leg, moveType: MoveType): WalkRoute {
        return WalkRoute(
            distance = leg.distance,
            end = with(leg.end) {
                Place(
                    name = name,
                    coordinate = Coordinate(
                        latitude = lat.toString(),
                        longitude = lon.toString()
                    )
                )
            },
            mode = moveType,
            sectionTime = leg.sectionTime,
            start = with(leg.start) {
                Place(
                    name = name,
                    coordinate = Coordinate(
                        latitude = lat.toString(),
                        longitude = lon.toString()
                    )
                )
            },
            steps = leg.steps?.map { step ->
                with(step) {
                    Step(
                        description = description,
                        distance = distance,
                        lineString = linestring,
                        streetName = streetName,
                    )
                }
            } ?: listOf(),
        )
    }

    companion object {
        private const val NOT_REGISTERED_STATION = "등록되지 않은 전철역입니다."
        private const val NO_SUPPORTING_TYPE = "지원하지 않는 타입입니다."
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