package com.stop.data.remote.model.route.gyeonggi

import com.stop.domain.model.route.gyeonggi.GyeonggiBusRouteIdResponse
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
internal data class GyeonggiBusRouteIdResponse(
    @Path("msgBody")
    @Element(name = "busRouteList")
    val routeList: List<GyeonggiBusRoute>
) {
    fun toDomain(): GyeonggiBusRouteIdResponse {
        return GyeonggiBusRouteIdResponse(
            routeList = routeList.map {
                it.toDomain()
            }
        )
    }
}
