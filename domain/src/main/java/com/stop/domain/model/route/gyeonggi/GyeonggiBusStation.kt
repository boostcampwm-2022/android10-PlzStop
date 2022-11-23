package com.stop.domain.model.route.gyeonggi

import javax.xml.bind.annotation.XmlAccessType
import javax.xml.bind.annotation.XmlAccessorType
import javax.xml.bind.annotation.XmlElement
import javax.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "busStationList")
@XmlAccessorType(XmlAccessType.FIELD)
data class GyeonggiBusStation(
    val stationId: Int,
    val stationName: String,
    @field:XmlElement(name = "x")
    val longitude: String,
    @field:XmlElement(name = "y")
    val latitude: String,
)
