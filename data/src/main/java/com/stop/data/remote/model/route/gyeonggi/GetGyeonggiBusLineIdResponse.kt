package com.stop.data.remote.model.route.gyeonggi

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
data class GetGyeonggiBusLineIdResponse(
    @Element(name = "msgBody")
    val msgBody: BusLineIdMsgBody,
) {
    fun toDomain(): com.stop.domain.model.route.gyeonggi.GetGyeonggiBusLineIdResponse {
        return com.stop.domain.model.route.gyeonggi.GetGyeonggiBusLineIdResponse(
            msgBody = msgBody.toDomain()
        )
    }
}
