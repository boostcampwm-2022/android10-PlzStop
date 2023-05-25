package com.stop.domain.model.route.tmap.origin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Itinerary(
    val fare: Fare,
    val legs: List<Leg>,
    val pathType: Int,
    val totalDistance: Double,
    val totalTime: Int,
    val transferCount: Int,
    val totalWalkDistance: Double,
    val totalWalkTime: Int
)