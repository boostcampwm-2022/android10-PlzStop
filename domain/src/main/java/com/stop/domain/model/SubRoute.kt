package com.stop.domain.model

interface SubRoute {
    val distance: Double // distance
    val estimatedTime: Int // sectionTime
    val moveType: MoveType // 이동 타입
    val startPlace: Place // start 출발지
    val endPlace: Place // end 도착지
}