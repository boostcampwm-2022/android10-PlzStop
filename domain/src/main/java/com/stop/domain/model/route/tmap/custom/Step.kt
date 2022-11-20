package com.stop.domain.model.route.tmap.custom

data class Step(
    val description: String,
    val distance: Int,
    val lineString: String,
    val streetName: String
)