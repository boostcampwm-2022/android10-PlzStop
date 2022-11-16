package com.stop.data.remote.model.route

import com.squareup.moshi.Json
import com.stop.domain.model.*
import com.stop.domain.model.RouteResponse
import com.stop.domain.model.Itinerary

data class RouteResponse(
    @Json(name = "metaData")
    val metaData: MetaData
) {
    fun toDomain(): RouteResponse {
        val itineraries: List<Itinerary> = this.metaData.plan.itineraries.map { itinerary ->
            val result = itinerary.legs.fold(listOf<Route>()) { routes, leg ->
                try {
                    val moveType = MoveType.getMoveTypeByName(leg.mode)

                    return@fold routes + when (moveType) {
                        MoveType.BUS, MoveType.SUBWAY -> createTransportRoute(leg, moveType)
                        MoveType.WALK -> createWalkRoute(leg, moveType)
                        else -> return@fold routes
                    }
                } catch (_: IllegalArgumentException) {
                    return@fold routes
                }
            }
            Itinerary(
                totalFare = itinerary.fare.regular.totalFare.toString(),
                routes = result,
                totalDistance = itinerary.totalDistance,
                totalTime = itinerary.totalTime,
                transferCount = itinerary.transferCount,
                walkTime = itinerary.walkTime,
            )
        }

        return RouteResponse(itineraries)
    }

    private fun createTransportRoute(leg: Leg, moveType: MoveType): PublicTransportRoute {
        return PublicTransportRoute(
            distance = leg.distance,
            end = leg.end.toDomain(),
            mode = moveType,
            sectionTime = leg.sectionTime,
            start = leg.start.toDomain(),
            lines = createCoordinates(leg.passShape?.linestring ?: ""),
            stations = leg.passStopList?.stationList?.map { it.toDomain() } ?: listOf(),
            routeInfo = leg.route ?: "",
            routeColor = leg.routeColor ?: "",
            routeType = leg.type ?: -1,
        )
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
            end = leg.end.toDomain(),
            mode = moveType,
            sectionTime = leg.sectionTime,
            start = leg.start.toDomain(),
            steps = leg.steps?.map { it.toDomain() } ?: listOf(),
        )
    }
}