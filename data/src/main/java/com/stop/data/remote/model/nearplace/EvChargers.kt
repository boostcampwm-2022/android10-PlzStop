package com.stop.data.remote.model.nearplace

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EvChargers(
    val evCharger: List<Any>
)