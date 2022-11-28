package com.stop.data.remote.model.route.gyeonggi

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "busRouteInfoItem")
internal data class GyeonggiBusLastTime(
    @PropertyElement(name = "upLastTime")
    val upLastTime: String, // 평일 기점에서 출발하는 막차 시간
    @PropertyElement(name = "startStationId")
    val startStationId: String, // 기점 정류소 아이디
    @PropertyElement(name = "startStationName")
    val startStationName: String, // 기점 정류소 이름
    @PropertyElement(name = "downLastTime")
    val downLastTime: String, // 평일 종점에서 출발하는 막차 시간
    @PropertyElement(name = "endStationId")
    val endStationId: String, // 종점 정류소 아이디
    @PropertyElement(name = "endStationName")
    val endStationName: String, // 종점 정류소 이름
    @PropertyElement(name = "peekAlloc")
    val minTerm: String, // 최소 배차시간
    @PropertyElement(name = "nPeekAlloc")
    val maxTerm: String, // 최대 배차시간
) {
    fun toDomain(): com.stop.domain.model.route.gyeonggi.GyeonggiBusLastTime {
        return com.stop.domain.model.route.gyeonggi.GyeonggiBusLastTime(
            upLastTime = upLastTime,
            startStationId = startStationId,
            startStationName = startStationName,
            downLastTime = downLastTime,
            endStationId = endStationId,
            endStationName = endStationName,
            minTerm = minTerm,
            maxTerm = maxTerm,
        )
    }
}
