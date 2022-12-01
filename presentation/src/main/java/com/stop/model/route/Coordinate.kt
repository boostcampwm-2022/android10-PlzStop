package com.stop.model.route

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Coordinate(
    val latitude: String,
    val longitude: String,
) : Parcelable
