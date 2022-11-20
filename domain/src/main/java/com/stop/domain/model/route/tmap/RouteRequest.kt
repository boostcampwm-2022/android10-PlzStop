package com.stop.domain.model.route.tmap

data class RouteRequest(
    val startX: String,
    val startY: String,
    val endX: String,
    val endY: String,
    val lang: Int = 0,
    val format: String = "json",
    val count: Int = 10,
)