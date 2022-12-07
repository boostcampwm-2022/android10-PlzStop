package com.stop.data.remote.model.nearplace

import com.squareup.moshi.JsonClass
import com.stop.data.model.nearplace.PlaceRepositoryItem
import kotlin.math.round

@JsonClass(generateAdapter = true)
data class Poi(
    val bizName: String,
    val collectionType: String,
    val dataKind: String,
    val desc: String,
    val detailAddrName: String,
    val detailBizName: String,
    val detailInfoFlag: String,
    val evChargers: EvChargers,
    val firstBuildNo: String,
    val firstNo: String,
    val frontLat: String,
    val frontLon: String,
    val id: String,
    val lowerAddrName: String,
    val lowerBizName: String,
    val middleAddrName: String,
    val middleBizName: String,
    val mlClass: String,
    val name: String,
    val navSeq: String,
    val newAddressList: NewAddressList,
    val noorLat: String,
    val noorLon: String,
    val parkFlag: String,
    val pkey: String,
    val radius: String,
    val roadName: String,
    val rpFlag: String,
    val secondBuildNo: String,
    val secondNo: String,
    val telNo: String,
    val upperAddrName: String,
    val upperBizName: String,
    val zipCode: String
) {

    fun toRepositoryModel(): PlaceRepositoryItem {
        val road = newAddressList.newAddress.firstOrNull()
        return PlaceRepositoryItem(
            name = name,
            radius = (round(radius.toDouble() * 100) / 100).toString(),
            fullAddressRoad = road?.fullAddressRoad ?: "",
            centerLat = road?.centerLat?.toDouble() ?: 0.0,
            centerLon = road?.centerLon?.toDouble() ?: 0.0
        )
    }

}