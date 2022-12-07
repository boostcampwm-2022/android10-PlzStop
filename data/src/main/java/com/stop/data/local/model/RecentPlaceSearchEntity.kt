package com.stop.data.local.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.stop.data.model.nearplace.PlaceRepositoryItem

@Entity(indices = [Index(value = ["name"], unique = true)])
data class RecentPlaceSearchEntity(
    val name: String,
    val radius: String,
    val fullAddressRoad: String,
    val centerLat: Double,
    val centerLon: Double,
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
) {

    fun toRepositoryModel() = PlaceRepositoryItem(
        name = name,
        radius = radius,
        fullAddressRoad = fullAddressRoad,
        centerLat = centerLat,
        centerLon = centerLon,
    )

}