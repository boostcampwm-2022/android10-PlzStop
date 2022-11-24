package com.stop.domain.model.nowlocation

data class BusInfoUseCaseItem(
    val isArrivedAtStation: String,
    val sectionId: String,
    val latitude: String,
    val longitude: String,
    val busId: String,
    val busNumber: String,
    val isRun: String,
)
