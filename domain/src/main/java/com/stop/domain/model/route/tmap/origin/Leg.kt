package com.stop.domain.model.route.tmap.origin

import com.squareup.moshi.JsonClass
import com.stop.domain.model.nowlocation.Location
import com.stop.domain.model.nowlocation.SubwayRoadLocationUseCaseItem

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
    fun toUseCaseModel(): SubwayRoadLocationUseCaseItem {
        val line = passShape?.linestring?.split(" ")?.map {
            val (lat, long) = it.split(",")
            Location(lat.toDouble(), long.toDouble())
        } ?: emptyList()
        return SubwayRoadLocationUseCaseItem(
            line = line,
            sectionTime = sectionTime
        )
    }
}