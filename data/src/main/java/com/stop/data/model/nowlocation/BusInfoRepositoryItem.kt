package com.stop.data.model.nowlocation

import com.stop.domain.model.nowlocation.BusInfoUseCaseItem

data class BusInfoRepositoryItem(
    val isArrivedAtStation: String,
    val sectionId: String,
    val latitude: String,
    val longitude: String,
    val busId: String,
    val busNumber: String,
    val isRun: String,
) {
    fun toUseCaseModel() = BusInfoUseCaseItem(
        isArrivedAtStation,
        sectionId,
        latitude,
        longitude,
        busId,
        busNumber,
        isRun,
    )
}
