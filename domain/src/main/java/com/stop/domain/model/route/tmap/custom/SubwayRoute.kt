package com.stop.domain.model.route.tmap.custom

data class SubwayRoute(
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