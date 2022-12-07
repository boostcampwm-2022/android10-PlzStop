package com.stop.model.route

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
    val name: String,
    val coordinate: Coordinate,
    val roadAddress: String? = null,
) : Parcelable
