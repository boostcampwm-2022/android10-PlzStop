package com.stop.data.remote.model.route

import com.squareup.moshi.Json
import com.stop.domain.model.Coordinate
import com.stop.domain.model.Station

data class Station(
    @Json(name = "index")
    val index: Int,
    @Json(name = "lat")
    val lat: String,
    @Json(name = "lon")
    val lon: String,
    @Json(name = "stationID")
    val stationID: String,
    @Json(name = "stationName")
    val stationName: String
) {
    fun toDomain(): Station {
        return Station(
            orderIndex = index,
            coordinate = Coordinate(latitude = lat, longitude = lon),
            stationId = stationID,
            stationName = stationName,
        )
    }
}