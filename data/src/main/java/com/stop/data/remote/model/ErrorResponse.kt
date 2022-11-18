package com.stop.data.remote.model

import com.squareup.moshi.JsonClass
import com.stop.data.remote.model.Error

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val error: Error
)