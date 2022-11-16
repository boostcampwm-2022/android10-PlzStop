package com.stop.data.remote.model.route

import com.squareup.moshi.Json

data class Currency(
    @Json(name = "currency")
    val currency: String,
    @Json(name = "currencyCode")
    val currencyCode: String,
    @Json(name = "symbol")
    val symbol: String
)