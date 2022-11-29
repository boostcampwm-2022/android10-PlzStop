package com.stop.data.remote.model.route.gyeonggi

import com.stop.domain.model.route.gyeonggi.GyeonggiBusRouteStationsResponse
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
internal data class GyeonggiBusRouteStationsResponse(
    @Element(name = "msgBody")
    val msgBody: BusRouteStationsMsgBody
) {
    fun toDomain(): GyeonggiBusRouteStationsResponse {
        return GyeonggiBusRouteStationsResponse(
            msgBody = msgBody.toDomain()
        )
    }
}