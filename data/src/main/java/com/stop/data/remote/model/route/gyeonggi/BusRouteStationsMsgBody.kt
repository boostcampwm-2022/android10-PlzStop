package com.stop.data.remote.model.route.gyeonggi

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "msgBody")
internal data class BusRouteStationsMsgBody(
    @Element(name = "busRouteStationList")
    val stations: List<GyeonggiBusStation>
) {
    fun toDomain(): com.stop.domain.model.route.gyeonggi.BusRouteStationsMsgBody {
        return com.stop.domain.model.route.gyeonggi.BusRouteStationsMsgBody(
            stations = stations.map { it.toDomain() }
        )
    }
}