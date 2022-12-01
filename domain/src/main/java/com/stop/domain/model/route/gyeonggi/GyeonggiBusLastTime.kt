package com.stop.domain.model.route.gyeonggi

data class GyeonggiBusLastTime(
    val upLastTime: String, // 평일 기점에서 출발하는 막차 시간
    val startStationId: String, // 기점 정류소 아이디
    val startStationName: String, // 기점 정류소 이름
    val downLastTime: String, // 평일 종점에서 출발하는 막차 시간
    val endStationId: String, // 종점 정류소 아이디
    val endStationName: String, // 종점 정류소 이름
    val minTerm: String, // 최소 배차시간
    val maxTerm: String, // 최대 배차시간
)
