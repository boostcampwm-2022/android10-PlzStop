package com.stop.data.remote.model.nowlocation.bus

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BusNowLocationResponse(
    @Json(name = "msgBody")
    val busBody: BusBody,
)
