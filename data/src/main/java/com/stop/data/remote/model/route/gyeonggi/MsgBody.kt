package com.stop.data.remote.model.route.gyeonggi

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "msgBody")
internal data class MsgBody(
    @Element(name = "busStationList")
    val busStations: List<GyeonggiBusStation>
) {
    fun toDomain(): com.stop.domain.model.route.gyeonggi.MsgBody {
        return com.stop.domain.model.route.gyeonggi.MsgBody(busStations = busStations.map {
            it.toDomain()
        })
    }
}
