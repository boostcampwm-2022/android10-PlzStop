package com.stop.domain.model.route.tmap.custom

interface Route {
    val distance: Double
    val end: Place
    val mode: MoveType
    val sectionTime: Double
    val start: Place
}