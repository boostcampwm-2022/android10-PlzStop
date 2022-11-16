package com.stop.domain.model

data class PublicTransportRoute(
    override val distance: Double,
    override val end: Place,
    override val mode: MoveType,
    override val sectionTime: Double,
    override val start: Place,
    val lines: List<Coordinate>,
    val stations: List<Station>,
    val routeInfo: String,
    val routeColor: String,
    val routeType: Int,
): Route
