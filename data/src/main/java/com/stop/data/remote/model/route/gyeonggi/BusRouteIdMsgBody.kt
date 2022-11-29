package com.stop.data.remote.model.route.gyeonggi

import com.stop.domain.model.route.gyeonggi.BusRouteIdMsgBody
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "msgBody")
internal data class BusRouteIdMsgBody(
    @Element(name = "busRouteList")
    val routeList: List<GyeonggiBusRoute>
) {
    fun toDomain(): BusRouteIdMsgBody {
        return BusRouteIdMsgBody(routeList = routeList.map {
            it.toDomain()
        })
    }
}
