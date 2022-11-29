package com.stop.data.remote.model.nowlocation.subway

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TrainLocationInfo(
    val subwayId: String,
    @Json(name = "subwayNm")
    val subwayName: String,
    @Json(name = "statnId")
    val stationId: String,
    @Json(name = "statnNm")
    val stationName: String,
    @Json(name = "trainNo")
    val trainNumber: String,
    @Json(name = "trainSttus")
    val trainStatus: String // 0:진입 1:도착, 0,1외 나머지는:출발
)
