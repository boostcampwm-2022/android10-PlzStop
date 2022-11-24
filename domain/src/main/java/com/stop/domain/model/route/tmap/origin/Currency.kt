package com.stop.domain.model.route.tmap.origin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Currency(
    val currency: String,
    val currencyCode: String,
    val symbol: String
)