package com.stop.data.remote.model.route

data class Itinerary(
    val fare: Fare,
    val legs: List<Leg>,
    val pathType: Int,
    val totalDistance: Int,
    val totalTime: Int,
    val transferCount: Int,
    val walkDistance: Int,
    val walkTime: Int
)