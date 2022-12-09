package com.stop.model.route

import com.stop.domain.model.route.tmap.custom.Coordinate

data class RouteItem(
    val name: String,
    val coordinate: Coordinate,
    val mode: Int,
    val distance: Double,
    val travelTime: Int,
    val lastTime: String?,
    val beforeColor: Int,
    val currentColor: Int,
    val type: RouteItemType,
    val typeName: String = "",
)

fun RouteItem.toFirstRouteItem(): RouteItem {
    return this.copy(
        type = RouteItemType.FIRST
    )
}

fun RouteItem.toLastRouteItem(
    name: String,
    coordinate: com.stop.model.route.Coordinate
): RouteItem {
    return this.copy(
        name = name,
        coordinate = Coordinate(coordinate.latitude, coordinate.longitude),
        beforeColor = currentColor,
        type = RouteItemType.LAST
    )
}