package com.stop.data.remote.model.route

import com.squareup.moshi.Json

data class Fare(
    @Json(name = "regular")
    val regular: Regular
)