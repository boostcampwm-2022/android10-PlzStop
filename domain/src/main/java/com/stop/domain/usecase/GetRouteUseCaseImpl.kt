package com.stop.domain.usecase

import com.squareup.moshi.JsonDataException
import com.stop.domain.model.geoLocation.AddressType
import com.stop.domain.model.route.gyeonggi.GyeonggiBusStation
import com.stop.domain.model.route.seoul.bus.BusStationInfo
import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.model.route.tmap.custom.*
import com.stop.domain.model.route.tmap.origin.Leg
import com.stop.domain.repository.RouteRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlin.math.abs

internal class GetRouteUseCaseImpl @Inject constructor(
    private val routeRepository: RouteRepository
) : GetRouteUseCase {

    /*
        T MAP에서 제공하는 신분당선의 외부코드(FR_CODE)는 공공데이터 포털에서 사용하는 외부 코드와
        일치하지 않습니다. 그래서 신분당선의 경우 전철역코드를 하드코딩해서 제공하는 방식으로
        구현했습니다.
     */
    private val shinbundangLineCd = mapOf(
        "강남" to "4307",
        "양재" to "4308",
        "신사" to "4304",
        "논현" to "4305",
        "신논현" to "4306",
        "양재시민의숲" to "4309",
        "청계산입구" to "4310",
        "판교" to "4311",
        "정자" to "4312",
        "미금" to "4313",
        "동천" to "4314",
        "수지구청" to "4315",
        "성복" to "4316",
        "상현" to "4317",
        "광교중앙" to "4318",
        "광교" to "4319",
    )

    override suspend fun getRoute(routeRequest: RouteRequest): List<Itinerary> {
        val originRouteData = routeRepository.getRoute(routeRequest)

        return originRouteData.metaData.plan.itineraries.fold(listOf()) itinerary@{ itineraries, itinerary ->
            val result = itinerary.legs.fold(listOf<Route>()) { routes, leg ->
                try {
                    val moveType = MoveType.getMoveTypeByName(leg.mode)

                    routes + when (moveType) {
                        MoveType.SUBWAY, MoveType.BUS -> createPublicTransportRoute(
                            leg,
                            moveType,
                        )
                        MoveType.WALK -> createWalkRoute(leg, moveType)
                        else -> return@fold routes
                    }
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    if (e.message?.contains(GYEONGGI_REGION_BUS_NOT_SUPPORT) == true) {
                        return@itinerary itineraries
                    }
                    routes
                } catch (e: JsonDataException) { // T MAP에 있는 정류소 이름이 업데이트 되지 않아, 공공 데이터 포털의 결과가 없는 경우 처리
                    e.printStackTrace()
                    routes
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


    private suspend fun createPublicTransportRoute(
        leg: Leg,
        moveType: MoveType,
    ): SubwayRoute {
        return SubwayRoute(
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
            stations = leg.passStopList?.stationList?.mapIndexed { mapIndex, station ->
                val id = if (mapIndex == 0) { // 승차지의 막차 시간만 필요합니다
                    getIdUsedAtPublicApi(leg.route, station, moveType)
                } else {
                    UNKNOWN_ARS_ID
                }
                with(station) {
                    Station(
                        orderIndex = index,
                        coordinate = Coordinate(
                            latitude = lat,
                            longitude = lon
                        ),
                        stationId = stationID,
                        stationName = stationName,
                        idUsedAtPublicApi = id,
                    )
                }
            } ?: listOf(),
            routeInfo = leg.route ?: "",
            routeColor = leg.routeColor ?: "",
            routeType = leg.type ?: -1,
        )
    }

    private suspend fun getIdUsedAtPublicApi(
        route: String?,
        station: com.stop.domain.model.route.tmap.origin.Station,
        moveType: MoveType,
    ): String {
        return when (moveType) {
            MoveType.SUBWAY -> {
                if (route == "신분당선") {
                    return shinbundangLineCd[station.stationName] ?: throw IllegalArgumentException(
                        NOT_REGISTERED_STATION
                    )
                }
                routeRepository.getSubwayStationCd(station.stationID, station.stationName)
            }
            MoveType.BUS -> {
                if (route?.contains("마을") == true) {
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
        delay(200) // 초당 처리 횟수를 초과하지 않기 위해 딜레이를 넣음
        val reverseGeocodingResponse =
            routeRepository.reverseGeocoding(Coordinate(station.lat, station.lon), AddressType.LOT_ADDRESS)
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
    }
}