package com.stop.data.remote.model.route.gyeonggi

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "msgBody")
internal data class BusLastTimeMsgBody(
    @Element(name = "busRouteInfoItem")
    val routeList: List<GyeonggiBusLastTime>
) {
    fun toDomain(): com.stop.domain.model.route.gyeonggi.BusLastTimeMsgBody {
        return com.stop.domain.model.route.gyeonggi.BusLastTimeMsgBody(
            routeList = routeList.map {
                it.toDomain()
            }
        )
    }
}
