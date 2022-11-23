package com.stop.domain.model.route.seoul.subway

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchInfoBySubwayNameService(
    @Json(name = "list_total_count")
    val listTotalCount: Int,
    @Json(name = "RESULT")
    val result: RESULT,
    @Json(name = "row")
    val row: List<Row>
)