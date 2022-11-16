package com.stop.data.remote.model.route

import com.squareup.moshi.Json

data class PassShape(
    @Json(name = "linestring")
    val linestring: String
)