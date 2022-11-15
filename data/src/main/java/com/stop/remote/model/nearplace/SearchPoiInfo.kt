package com.stop.remote.model.nearplace

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchPoiInfo(
    val count: String,
    val page: String,
    val pois: Pois,
    val totalCount: String
)