package com.stop.data.remote.model.route.gyeonggi

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
internal data class GetGyeonggiBusRouteStationsResponse(
    @Element(name = "msgBody")
    val msgBody: BusRouteStationsMsgBody
) {
    fun toDomain(): com.stop.domain.model.route.gyeonggi.GetGyeonggiBusRouteStationsResponse {
        return com.stop.domain.model.route.gyeonggi.GetGyeonggiBusRouteStationsResponse(
            msgBody = msgBody.toDomain()
        )
    }
}