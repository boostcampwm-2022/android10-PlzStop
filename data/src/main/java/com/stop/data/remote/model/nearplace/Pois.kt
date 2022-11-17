package com.stop.data.remote.model.nearplace

import com.squareup.moshi.JsonClass
import com.stop.data.remote.model.nearplace.Poi

@JsonClass(generateAdapter = true)
data class Pois(
    val poi: List<Poi>
)