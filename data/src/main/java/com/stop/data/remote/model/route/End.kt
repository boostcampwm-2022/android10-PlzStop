package com.stop.data.remote.model.route

import com.squareup.moshi.Json

data class End(
    @Json(name = "lat")
    val lat: Double,
    @Json(name = "lon")
    val lon: Double,
    @Json(name = "name")
    val name: String
)