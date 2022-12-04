package com.stop.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stop.data.model.nearplace.PlaceRepositoryItem

@Entity
data class RecentPlaceSearchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = -1,
    val name: String,
    val radius: String,
    val fullAddressRoad: String,
    val centerLat: Double,
    val centerLon: Double
) {

    fun toRepositoryModel() = PlaceRepositoryItem(
        name = name,
        radius = radius,
        fullAddressRoad = fullAddressRoad,
        centerLat = centerLat,
        centerLon = centerLon,
    )

}