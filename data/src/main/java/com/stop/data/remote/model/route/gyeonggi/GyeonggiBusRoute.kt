package com.stop.data.remote.model.route.gyeonggi

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "busRouteList")
data class GyeonggiBusRoute(
    @PropertyElement(name = "routeId")
    val routeId: String,
    @PropertyElement(name = "routeName")
    val busName: String,
) {
    fun toDomain(): com.stop.domain.model.route.gyeonggi.GyeonggiBusRoute {
        return com.stop.domain.model.route.gyeonggi.GyeonggiBusRoute(
            routeId = routeId,
            busName = busName,
        )
    }
}
