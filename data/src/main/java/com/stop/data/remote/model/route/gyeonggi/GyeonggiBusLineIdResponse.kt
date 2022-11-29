package com.stop.data.remote.model.route.gyeonggi

import com.stop.domain.model.route.gyeonggi.GyeonggiBusLineIdResponse
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
internal data class GyeonggiBusLineIdResponse(
    @Element(name = "msgBody")
    val msgBody: BusLineIdMsgBody,
) {
    fun toDomain(): GyeonggiBusLineIdResponse {
        return GyeonggiBusLineIdResponse(
            msgBody = msgBody.toDomain()
        )
    }
}
