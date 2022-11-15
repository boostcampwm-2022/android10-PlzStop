package com.stop.domain.model

data class RouteResponse (
    val totalTime: Int,
    val totalFare: String,
    val totalDistance: Double, // 총 이동거리
    val transferCount: Int, // 환승 횟수
    val walkTime: Int, // 걷는 시간
    val subRoutes: List<SubRoute>
)