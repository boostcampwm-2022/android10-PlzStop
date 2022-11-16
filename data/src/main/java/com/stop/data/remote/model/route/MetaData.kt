package com.stop.data.remote.model.route

import com.squareup.moshi.Json

data class MetaData(
    @Json(name = "plan")
    val plan: Plan,
    @Json(name = "requestParameters")
    val requestParameters: RequestParameters
)