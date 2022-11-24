package com.stop.domain.model.route

import com.stop.domain.model.route.tmap.custom.Coordinate

data class TransportIdRequest(
    val transportMoveType: TransportMoveType,
    val id: String,
    val stationName: String,
    val coordinate: Coordinate,
    val stationType: Int,
) {
    fun changeId(newId: String): TransportIdRequest {
        return TransportIdRequest(
            transportMoveType = transportMoveType,
            id = newId,
            stationName = stationName,
            coordinate = coordinate,
            stationType = stationType,
        )
    }
}
