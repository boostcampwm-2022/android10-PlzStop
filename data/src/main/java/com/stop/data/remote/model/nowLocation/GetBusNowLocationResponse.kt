package com.stop.data.remote.model.nowLocation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetBusNowLocationResponse(
    @Json(name = "msgBody")
    val busBody: BusBody,
)
