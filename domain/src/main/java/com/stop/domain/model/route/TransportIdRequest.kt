package com.stop.domain.model.route

import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.domain.model.route.tmap.custom.Place

data class TransportIdRequest(
    val transportMoveType: TransportMoveType,
    val stationId: String,
    val stationName: String,
    val coordinate: Coordinate,
    val stationType: Int,
    val area: Area,
    val routeId: String,
    val routeName: String,
    val term: Int, // 배차 간격, 서울 버스는 노선을 구하는 과정에서 얻을 수 있기 때문에 넣음
    val destinationStation: Place,
    val destinationStationId: String,
    val sectionTime: Int,
    val cumulativeSectionTime: Int,
) {
    fun changeStartStationId(newStationId: String): TransportIdRequest {
        return TransportIdRequest(
            transportMoveType = transportMoveType,
            stationId = newStationId,
            stationName = stationName,
            coordinate = coordinate,
            stationType = stationType,
            area = area,
            routeId = routeId,
            routeName = routeName,
            term = term,
            destinationStation = destinationStation,
            destinationStationId = destinationStationId,
            sectionTime = sectionTime,
            cumulativeSectionTime = cumulativeSectionTime,
        )
    }

    fun changeDestinationStationId(newDestinationStationId: String): TransportIdRequest {
        return TransportIdRequest(
            transportMoveType = transportMoveType,
            stationId = stationId,
            stationName = stationName,
            coordinate = coordinate,
            stationType = stationType,
            area = area,
            routeId = routeId,
            routeName = routeName,
            term = term,
            destinationStation = destinationStation,
            destinationStationId = newDestinationStationId,
            sectionTime = sectionTime,
            cumulativeSectionTime = cumulativeSectionTime,
        )
    }

    fun changeRouteId(newRouteId: String, newTerm: Int?): TransportIdRequest {
        return TransportIdRequest(
            transportMoveType = transportMoveType,
            stationId = stationId,
            stationName = stationName,
            coordinate = coordinate,
            stationType = stationType,
            area = area,
            routeId = newRouteId,
            routeName = routeName,
            term = newTerm ?: term,
            destinationStation = destinationStation,
            destinationStationId = destinationStationId,
            sectionTime = sectionTime,
            cumulativeSectionTime = cumulativeSectionTime,
        )
    }
}
