package com.stop.domain.model.route.tmap.origin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Regular(
    val currency: Currency,
    val totalFare: Int
)