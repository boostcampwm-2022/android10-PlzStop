package com.stop.data.remote.model.nowlocation.bus

import com.squareup.moshi.Json
import com.stop.domain.model.nowlocation.BusCurrentInformation

data class BusBody(
    @Json(name = "itemList")
    val busCurrentInformation: List<BusCurrentInformation>?
)
