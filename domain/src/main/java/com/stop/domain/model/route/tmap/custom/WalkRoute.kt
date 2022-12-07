package com.stop.domain.model.route.tmap.custom

data class WalkRoute(
    override val distance: Double,
    override val end: Place,
    override val mode: MoveType,
    override val sectionTime: Double,
    override val proportionOfSectionTime: Float,
    override val start: Place,
    val steps: List<Step>
): Route
