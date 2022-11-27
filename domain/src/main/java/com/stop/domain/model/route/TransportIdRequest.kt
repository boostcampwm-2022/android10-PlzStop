package com.stop.domain.model.route

import com.stop.domain.model.route.tmap.custom.Coordinate

data class TransportIdRequest(
    val transportMoveType: TransportMoveType,
    val stationId: String,
    val stationName: String,
    val coordinate: Coordinate,
    val stationType: Int,
    val area: Area,
    val lineId: String,
    val lineName: String,
    val term: Int, // 배차 간격, 서울 버스는 노선을 구하는 과정에서 얻을 수 있기 때문에 넣음
) {
    fun changeStationId(newStationId: String): TransportIdRequest {
        return TransportIdRequest(
            transportMoveType = transportMoveType,
            stationId = newStationId,
            stationName = stationName,
            coordinate = coordinate,
            stationType = stationType,
            area = area,
            lineId = lineId,
            lineName = lineName,
            term = term,
        )
    }

    fun changeLineId(newLineId: String, newTerm: Int?): TransportIdRequest {
        return TransportIdRequest(
            transportMoveType = transportMoveType,
            stationId = stationId,
            stationName = stationName,
            coordinate = coordinate,
            stationType = stationType,
            area = area,
            lineId = newLineId,
            lineName = lineName,
            term = newTerm ?: term,
        )
    }
}
