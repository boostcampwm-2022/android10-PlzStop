package com.stop.domain.model.route.gyeonggi

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlElementWrapper
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
data class GetGyeonggiBusStationIdResponse(
    @field:XmlElementWrapper(name = "msgBody")
    @field:XmlElement(name = "busStationList")
    val busStations: List<GyeonggiBusStation>
)
