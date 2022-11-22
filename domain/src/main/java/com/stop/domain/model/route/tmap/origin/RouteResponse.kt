package com.stop.domain.model.route.tmap.origin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RouteResponse(
    val metaData: MetaData
)