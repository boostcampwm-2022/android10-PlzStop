package com.stop.domain.model.route.tmap.origin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Step(
    val description: String,
    val distance: Int,
    val linestring: String,
    val streetName: String
)