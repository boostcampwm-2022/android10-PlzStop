package com.stop.data.remote.model.route.gyeonggi

import com.stop.domain.model.route.gyeonggi.GyeonggiBusLastTimeResponse
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
internal data class GyeonggiBusLastTimeResponse(
    @Path("msgBody")
    @Element(name = "busRouteInfoItem")
    val routeList: List<GyeonggiBusLastTime>
) {
    fun toDomain(): GyeonggiBusLastTimeResponse {
        return GyeonggiBusLastTimeResponse(
            routeList = routeList.map {
                it.toDomain()
            }
        )
    }
}
