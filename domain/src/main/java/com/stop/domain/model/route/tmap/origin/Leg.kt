package com.stop.domain.model.route.tmap.origin

import com.squareup.moshi.JsonClass
import com.stop.domain.model.nowlocation.Location
import com.stop.domain.model.nowlocation.SubwayRouteLocationUseCaseItem

@JsonClass(generateAdapter = true)
data class Leg(
    val distance: Double,
    val end: End,
    val mode: String,
    val passShape: PassShape?,
    val passStopList: PassStopList?,
    val route: String?,
    val routeColor: String?,
    val sectionTime: Double,
    val start: Start,
    val steps: List<Step>?,
    val type: Int?
) {
    fun toUseCaseModel(): SubwayRouteLocationUseCaseItem {
        val line = passShape?.linestring?.split(" ")?.map {
            val (long, lat) = it.split(",")
            Location(lat.toDouble(), long.toDouble())
        } ?: emptyList()
        return SubwayRouteLocationUseCaseItem(
            line = line,
            sectionTime = sectionTime
        )
    }
}