package com.stop.domain.usecase.route

import com.stop.domain.model.route.tmap.RouteRequest
import com.stop.domain.model.route.tmap.custom.*
import com.stop.domain.model.route.tmap.origin.Leg
import com.stop.domain.repository.RouteRepository
import javax.inject.Inject

internal class GetRouteUseCaseImpl @Inject constructor(
    private val routeRepository: RouteRepository
) : GetRouteUseCase {

    private val nameParsingRegex = """\(+[^)]+\)""".toRegex()

    override suspend operator fun invoke(routeRequest: RouteRequest): List<Itinerary> {
        val originItineraries = routeRepository.getRoute(routeRequest)

        if (originItineraries.isEmpty()) {
            return listOf()
        }

        return originItineraries.fold(listOf()) itinerary@{ itineraries, itinerary ->
            val result = itinerary.legs.fold(listOf<Route>()) { routes, leg ->
                try {
                    val moveType = MoveType.getMoveTypeByName(leg.mode)

                    routes + when (moveType) {
                        MoveType.SUBWAY, MoveType.BUS -> createPublicTransportRoute(leg, moveType, itinerary.totalTime)
                        MoveType.WALK, MoveType.TRANSFER -> createWalkRoute(leg, moveType, itinerary.totalTime)
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


    private fun createPublicTransportRoute(leg: Leg, moveType: MoveType, totalTime: Int): TransportRoute {
        return TransportRoute(
            distance = leg.distance,
            end = with(leg.end) {
                Place(
                    name = parsingName(name),
                    coordinate = Coordinate(
                        latitude = lat.toString(),
                        longitude = lon.toString()
                    )
                )
            },
            mode = moveType,
            sectionTime = leg.sectionTime,
            proportionOfSectionTime = calculateProportionOfSectionTime(leg.sectionTime, totalTime),
            start = with(leg.start) {
                Place(
                    name = parsingName(name),
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
                        stationName = parsingName(stationName),
                    )
                }
            } ?: listOf(),
            routeInfo = leg.route ?: "",
            routeColor = leg.routeColor ?: "",
            routeType = leg.type ?: -1,
        )
    }

    private fun parsingName(originName: String): String {
        return originName.replace(nameParsingRegex, "")
    }

    private fun calculateProportionOfSectionTime(sectionTime: Double, totalTime: Int): Float {
        return (sectionTime / totalTime * 10_000).toInt().toFloat() / 10000
    }

    private fun createCoordinates(linesString: String): List<Coordinate> {
        return linesString.split(" ").map {
            val (latitude, longitude) = it.split(",")
            Coordinate(latitude, longitude)
        }
    }

    private fun createWalkRoute(leg: Leg, moveType: MoveType, totalTime: Int): WalkRoute {
        return WalkRoute(
            distance = leg.distance,
            end = with(leg.end) {
                Place(
                    name = parsingName(name),
                    coordinate = Coordinate(
                        latitude = lat.toString(),
                        longitude = lon.toString()
                    )
                )
            },
            mode = moveType,
            sectionTime = leg.sectionTime,
            proportionOfSectionTime = calculateProportionOfSectionTime(leg.sectionTime, totalTime),
            start = with(leg.start) {
                Place(
                    name = parsingName(name),
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
}