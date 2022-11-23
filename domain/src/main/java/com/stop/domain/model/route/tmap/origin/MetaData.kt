package com.stop.domain.model.route.tmap.origin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MetaData(
    val plan: Plan,
    val requestParameters: RequestParameters
)