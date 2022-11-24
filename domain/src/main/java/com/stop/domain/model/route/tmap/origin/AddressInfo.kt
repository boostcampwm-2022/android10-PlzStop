package com.stop.domain.model.route.tmap.origin

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddressInfo(
    val fullAddress: String,
    @Json(name = "city_do")
    val city: String,
)
