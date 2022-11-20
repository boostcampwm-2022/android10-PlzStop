package com.stop.data.remote.model.geoLocation

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.stop.domain.model.geoLocation.GeoLocationInfo

@JsonClass(generateAdapter = true)
data class AddressInfo(
    val fullAddress: String,
    val addressType: String,
    @field:Json(name = "city_do") val cityDo: String,
    @field:Json(name = "gu_gun") val guGun: String,
    @field:Json(name = "eup_myun") val eupMyun: String,
    val adminDong: String,
    val adminDongCode: String,
    val legalDong: String,
    val legalDongCode: String,
    val ri: String,
    val bunji: String,
    val roadName: String,
    val buildingIndex: String,
    val buildingName: String,
    val mappingDistance: String,
    val roadCode: String
) {
    fun toRepositoryModel(): GeoLocationInfo {
        val address = fullAddress.split(",").drop(1)
        return GeoLocationInfo(
            title = buildingName,
            roadAddress = address.first(),
            lotAddress = address.last().replace(buildingName, ""),
            distance = mappingDistance
        )
    }
}
