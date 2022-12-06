package com.stop.domain.model.nowlocation

data class BusCurrentInformationUseCaseItem(
    val licensePlateNumber: String,
    val vehicleId: String,
    val currentStationOrder: String,
    val isStoppedAtStation: String,
    val longitude: String,
    val latitude: String,
    val isRun: String,
    val isLast: String,
    val beforeNodeId: String,
    val transportState: TransportState,
) {
    companion object {
        fun createDisappearItem(): BusCurrentInformationUseCaseItem {
            return BusCurrentInformationUseCaseItem(
                licensePlateNumber = "",
                vehicleId = "",
                currentStationOrder = "",
                isStoppedAtStation = "",
                longitude = "",
                latitude = "",
                isRun = "",
                isLast = "",
                beforeNodeId = "",
                transportState = TransportState.DISAPPEAR,
            )
        }
    }
}