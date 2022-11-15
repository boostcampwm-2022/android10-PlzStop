package com.stop.remote.model.nearplace

import com.squareup.moshi.JsonClass

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
)