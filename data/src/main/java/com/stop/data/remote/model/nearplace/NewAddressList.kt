package com.stop.data.remote.model.nearplace

import com.squareup.moshi.JsonClass
import com.stop.data.remote.model.nearplace.NewAddres

@JsonClass(generateAdapter = true)
data class NewAddressList(
    val newAddress: List<NewAddres>
)