package com.stop.data.remote.model.route.gyeonggi

import com.stop.domain.model.route.gyeonggi.GyeonggiBusStationIdResponse
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
internal data class GyeonggiBusStationIdResponse(
    @Path("msgBody")
    @Element(name = "busStationList")
    val busStations: List<GyeonggiBusStation>
) {
    fun toDomain(): GyeonggiBusStationIdResponse {
        return GyeonggiBusStationIdResponse(
            busStations = busStations.map {
                it.toDomain()
            }
        )
    }
}