package com.stop.data.remote.model.route.gyeonggi

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "busStationList")
internal data class GyeonggiBusStation(
    @PropertyElement(name = "stationId")
    val stationId: Int,
    @PropertyElement(name = "stationName")
    val stationName: String,
    @PropertyElement(name = "x")
    val longitude: String,
    @PropertyElement(name = "y")
    val latitude: String,
) {
    fun toDomain() = com.stop.domain.model.route.gyeonggi.GyeonggiBusStation(
        stationId = stationId,
        stationName = stationName,
        longitude = longitude,
        latitude = latitude,
    )
}
