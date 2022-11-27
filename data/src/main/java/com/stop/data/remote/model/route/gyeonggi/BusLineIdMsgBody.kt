package com.stop.data.remote.model.route.gyeonggi

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "msgBody")
data class BusLineIdMsgBody(
    @Element(name = "busRouteList")
    val routeList: List<GyeonggiBusRoute>
) {
    fun toDomain(): com.stop.domain.model.route.gyeonggi.BusLineIdMsgBody {
        return com.stop.domain.model.route.gyeonggi.BusLineIdMsgBody(routeList = routeList.map {
            it.toDomain()
        })
    }
}
