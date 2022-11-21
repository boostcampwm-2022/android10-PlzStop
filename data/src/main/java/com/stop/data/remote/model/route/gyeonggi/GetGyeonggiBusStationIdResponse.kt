package com.stop.data.remote.model.route.gyeonggi

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
data class GetGyeonggiBusStationIdResponse(
    @Element(name = "msgBody")
    val msgBody: MsgBody
) {
    fun toDomain(): com.stop.domain.model.route.gyeonggi.GetGyeonggiBusStationIdResponse {
        return com.stop.domain.model.route.gyeonggi.GetGyeonggiBusStationIdResponse(
            msgBody = msgBody.toDomain()
        )
    }
}
