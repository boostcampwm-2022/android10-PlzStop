package com.stop.data.remote.model.route.gyeonggi

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
internal data class GetGyeonggiBusLastTimeResponse(
    @Element(name = "msgBody")
    val msgBody: BusLastTimeMsgBody,
) {
    fun toDomain(): com.stop.domain.model.route.gyeonggi.GetGyeonggiBusLastTimeResponse {
        return com.stop.domain.model.route.gyeonggi.GetGyeonggiBusLastTimeResponse(
            msgBody = msgBody.toDomain()
        )
    }
}
