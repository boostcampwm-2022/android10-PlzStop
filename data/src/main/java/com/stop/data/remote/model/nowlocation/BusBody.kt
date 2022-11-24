package com.stop.data.remote.model.nowlocation

import com.squareup.moshi.Json

data class BusBody(
    @Json(name = "itemList")
    val busInfo: List<BusInfo>
)
