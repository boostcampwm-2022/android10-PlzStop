package com.stop.domain.model.nowlocation

data class SubwayRouteLocationUseCaseItem(
    val line: List<Location>,
    val sectionTime: Double
)
