package com.stop.domain.model.route.tmap.origin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class End(
    val lat: Double,
    val lon: Double,
    val name: String
)