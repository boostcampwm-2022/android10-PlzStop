package com.stop.domain.model.route.seoul.subway

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubwayStationsResponse(
    @Json(name = "SearchSTNBySubwayLineInfo")
    val searchStationNameBySubwayLineInfo: SearchStationNameBySubwayLineInfo
)
