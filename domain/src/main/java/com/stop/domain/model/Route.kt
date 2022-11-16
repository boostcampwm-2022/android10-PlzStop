package com.stop.domain.model

interface Route {

    val distance: Double
    val end: Place
    val mode: MoveType
    val sectionTime: Double
    val start: Place
}