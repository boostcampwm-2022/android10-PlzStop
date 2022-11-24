package com.stop.data.remote.model.nowlocation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetBusNowLocationResponse(
    @Json(name = "msgBody")
    val busBody: BusBody,
)
