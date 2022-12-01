package com.stop.data.remote.model.route.gyeonggi

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "response")
internal data class GyeonggiBusRouteStationsResponse(
    @Path("msgBody")
    @Element(name = "busRouteStationList")
    val stations: List<GyeonggiBusStation>
)