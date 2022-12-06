package com.stop.model.route

import com.stop.domain.model.route.tmap.custom.Coordinate
import com.stop.domain.model.route.tmap.custom.MoveType

data class RouteItem(
    val name: String,
    val coordinate: Coordinate,
    val mode: MoveType,
    val distance: Double,
    val travelTime: Int,
    val lastTime: String?,
    val beforeColor: Int,
    val currentColor: Int,
    val type: RouteItemType
)

fun RouteItem.toFirstRouteItem(): RouteItem {
    return this.copy(
        type = RouteItemType.FIRST
    )
}

fun RouteItem.toLastRouteItem(name: String, coordinate: com.stop.model.route.Coordinate): RouteItem {
    return this.copy(
        name = name,
        coordinate = Coordinate(coordinate.latitude, coordinate.longitude),
        type = RouteItemType.LAST
    )
}