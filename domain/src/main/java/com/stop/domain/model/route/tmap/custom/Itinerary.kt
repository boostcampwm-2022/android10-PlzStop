package com.stop.domain.model.route.tmap.custom

data class Itinerary(
    val totalFare: String,
    val routes: List<Route>,
    val totalDistance: Double, // 총 이동거리
    val totalTime: Int,
    val transferCount: Int, // 환승 횟수
    val walkTime: Int, // 걷는 시간
)
