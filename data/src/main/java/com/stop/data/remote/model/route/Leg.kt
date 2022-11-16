package com.stop.data.remote.model.route

import com.squareup.moshi.Json

data class Leg(
    @Json(name = "distance")
    val distance: Double,
    @Json(name = "end")
    val end: End,
    @Json(name = "mode")
    val mode: String,
    @Json(name = "passShape")
    val passShape: PassShape?, // 버스 혹은 지하철의 이동 경로
    @Json(name = "passStopList")
    val passStopList: PassStopList?,
    @Json(name = "route") // 버스 혹은 지하철의 이름 마을:02, 직행좌석:102, 수도권7호선
    val route: String?,
    @Json(name = "routeColor") // 버스 혹은 지하철의 고유 색상
    val routeColor: String?,
    @Json(name = "sectionTime")
    val sectionTime: Double,
    @Json(name = "start")
    val start: Start,
    @Json(name = "steps") // 도보 경로 정보
    val steps: List<Step>?,
    @Json(name = "type") // T MAP에서 사용하는 버스 혹은 지하철 구분 번호
    val type: Int?
)