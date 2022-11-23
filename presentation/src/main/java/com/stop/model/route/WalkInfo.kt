package com.stop.model.route

data class WalkInfo(
    val description: String, // description 이동 경로 설명
    val distance: Double, // 이동 경로 길이
    val walkPathCoordinates: List<Coordinate>, // linestring 이동 경로 좌표
)
