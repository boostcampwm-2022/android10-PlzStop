package com.stop.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Error(
    val category: String,
    val code: String,
    val id: String,
    val message: String
)