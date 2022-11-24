package com.stop.domain.model.route.seoul.bus

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetBusStationArsIdResponse(
    val msgBody: MsgBody,
)
