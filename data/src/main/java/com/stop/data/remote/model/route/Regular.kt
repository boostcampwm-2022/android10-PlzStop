package com.stop.data.remote.model.route

import com.squareup.moshi.Json

data class Regular(
    @Json(name = "currency")
    val currency: Currency,
    @Json(name = "totalFare")
    val totalFare: Int
)