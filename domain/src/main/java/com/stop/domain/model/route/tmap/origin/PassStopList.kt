package com.stop.domain.model.route.tmap.origin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PassStopList(
    val stationList: List<Station>
)