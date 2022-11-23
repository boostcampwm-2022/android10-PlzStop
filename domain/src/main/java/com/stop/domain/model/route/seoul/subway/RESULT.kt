package com.stop.domain.model.route.seoul.subway

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RESULT(
    @Json(name = "CODE")
    val code: String,
    @Json(name = "MESSAGE")
    val message: String
)