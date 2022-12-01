package com.stop.domain.model.route.seoul.bus

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BusLastTimeResponse(
    @Json(name = "msgBody")
    val lastTimeMsgBody: LastTimeMsgBody,
)
