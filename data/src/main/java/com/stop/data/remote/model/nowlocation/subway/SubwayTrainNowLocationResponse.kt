package com.stop.data.remote.model.nowlocation.subway

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubwayTrainNowLocationResponse(
    private val realtimePositionList: List<TrainLocationInfo>
)