package com.stop.data.remote.model.nowlocation.subway

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubwayTrainNowLocationResponse(
    @Json(name = "realtimePositionList")
    val realtimePositions: List<TrainLocationInfo>?
)