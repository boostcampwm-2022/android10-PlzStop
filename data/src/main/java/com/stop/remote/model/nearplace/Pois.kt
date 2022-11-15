package com.stop.remote.model.nearplace

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Pois(
    val poi: List<Poi>
)