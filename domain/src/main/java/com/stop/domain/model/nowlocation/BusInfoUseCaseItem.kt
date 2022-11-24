package com.stop.domain.model.nowlocation

data class BusInfoUseCaseItem(
    val isArrivedAtStation: String,
    val sectionId: String,
    val latitude: Double,
    val longitude: Double,
    val busId: String,
    val busNumber: String,
    val isRun: String,
)
