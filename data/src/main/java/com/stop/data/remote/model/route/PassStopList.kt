package com.stop.data.remote.model.route

import com.squareup.moshi.Json

data class PassStopList(
    @Json(name = "stationList")
    val stationList: List<Station>
)