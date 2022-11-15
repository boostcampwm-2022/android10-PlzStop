package com.stop.remote.model.nearplace


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NearPlcaeResponse(
    val searchPoiInfo: SearchPoiInfo
)