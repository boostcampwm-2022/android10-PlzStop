package com.stop.data.remote.model.route

import com.squareup.moshi.Json

data class Itinerary(
    @Json(name = "fare")
    val fare: Fare,
    @Json(name = "legs")
    val legs: List<Leg>,
    @Json(name = "pathType")
    val pathType: Int,
    @Json(name = "totalDistance")
    val totalDistance: Double,
    @Json(name = "totalTime")
    val totalTime: Int,
    @Json(name = "transferCount")
    val transferCount: Int,
    @Json(name = "walkDistance")
    val walkDistance: Double,
    @Json(name = "walkTime")
    val walkTime: Int
)