package com.stop.data.remote.model.route.gyeonggi

import com.stop.domain.model.route.gyeonggi.GyeonggiBusLastTimeResponse
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
internal data class GyeonggiBusLastTimeResponse(
    @Element(name = "msgBody")
    val msgBody: BusLastTimeMsgBody,
) {
    fun toDomain(): GyeonggiBusLastTimeResponse {
        return GyeonggiBusLastTimeResponse(
            msgBody = msgBody.toDomain()
        )
    }
}
