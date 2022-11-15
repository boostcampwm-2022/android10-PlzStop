package com.stop.data.remote.model.route

data class Step(
    val description: String,
    val distance: Int,
    val linestring: String,
    val streetName: String
)