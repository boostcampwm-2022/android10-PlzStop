package com.stop.data.remote.model.route.gyeonggi

import com.stop.domain.model.route.gyeonggi.GyeonggiBusRouteIdResponse
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
internal data class GyeonggiBusRouteIdResponse(
    @Element(name = "msgBody")
    val msgBody: BusRouteIdMsgBody,
) {
    fun toDomain(): GyeonggiBusRouteIdResponse {
        return GyeonggiBusRouteIdResponse(
            msgBody = msgBody.toDomain()
        )
    }
}
