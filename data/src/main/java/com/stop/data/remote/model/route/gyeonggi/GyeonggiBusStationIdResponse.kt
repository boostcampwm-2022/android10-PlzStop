package com.stop.data.remote.model.route.gyeonggi

import com.stop.domain.model.route.gyeonggi.GyeonggiBusStationIdResponse
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
internal data class GyeonggiBusStationIdResponse(
    @Element(name = "msgBody")
    val msgBody: MsgBody
) {
    fun toDomain(): GyeonggiBusStationIdResponse {
        return GyeonggiBusStationIdResponse(
            msgBody = msgBody.toDomain()
        )
    }
}
