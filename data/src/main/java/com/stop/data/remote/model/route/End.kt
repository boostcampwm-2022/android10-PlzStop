package com.stop.data.remote.model.route

import com.squareup.moshi.Json
import com.stop.domain.model.Coordinate
import com.stop.domain.model.Place

data class End(
    @Json(name = "lat")
    val lat: Double,
    @Json(name = "lon")
    val lon: Double,
    @Json(name = "name")
    val name: String
) {
    fun toDomain(): Place {
        return Place(
            name = name,
            coordinate = Coordinate(
                latitude = lat.toString(),
                longitude = lon.toString()
            )
        )
    }
}