package com.stop.data.remote.model.nearplace

import com.squareup.moshi.JsonClass
import com.stop.data.remote.model.nearplace.Pois

@JsonClass(generateAdapter = true)
data class SearchPoiInfo(
    val count: String,
    val page: String,
    val pois: Pois,
    val totalCount: String
)