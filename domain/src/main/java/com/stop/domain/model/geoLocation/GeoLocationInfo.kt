package com.stop.domain.model.geoLocation

data class GeoLocationInfo(
    val title: String,
    val roadAddress: String,
    val lotAddress: String,
    val distance: String
)

fun GeoLocationInfo.toClickedGeoLocationInfo(clickedPlaceName: String): GeoLocationInfo {
    return this.copy(
        title = clickedPlaceName
    )
}