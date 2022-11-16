package com.stop.data.remote.model.route

import com.squareup.moshi.Json

data class Plan(
    @Json(name = "itineraries")
    val itineraries: List<Itinerary>
)