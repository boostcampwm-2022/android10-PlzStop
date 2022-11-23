package com.stop.data.remote.model.nearplace

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NearPlaceResponse(
    val searchPoiInfo: SearchPoiInfo
)