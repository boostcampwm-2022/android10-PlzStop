package com.stop.data.remote.model.route

data class Leg(
    val distance: Int,
    val end: End,
    val mode: String,
    val passShape: PassShape,
    val passStopList: PassStopList,
    val route: String,
    val routeColor: String,
    val sectionTime: Int,
    val start: Start,
    val steps: List<Step>,
    val type: Int
)