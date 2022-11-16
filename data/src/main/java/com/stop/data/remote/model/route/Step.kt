package com.stop.data.remote.model.route

import com.squareup.moshi.Json
import com.stop.domain.model.Step

data class Step(
    @Json(name = "description")
    val description: String,
    @Json(name = "distance")
    val distance: Int,
    @Json(name = "linestring")
    val linestring: String,
    @Json(name = "streetName")
    val streetName: String
) {
    fun toDomain(): Step {
        return Step(
            description = description,
            distance = distance,
            lineString = linestring,
            streetName = streetName,
        )
    }
}