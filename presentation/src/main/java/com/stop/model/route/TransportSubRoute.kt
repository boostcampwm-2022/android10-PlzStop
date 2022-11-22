package com.stop.model.route

data class TransportSubRoute(
    override val distance: Double,
    override val estimatedTime: Int,
    override val moveType: MoveType,
    override val startPlace: Place,
    override val endPlace: Place,
    val routeColor: String, // 대중교통 노선 색상 ex) 지하철 호선 별 고유 색깔, 버스 별 고유 색깔
    val transportPathCoordinates: List<Coordinate>, // passShape -> linestring 대중교통 구간 좌표
    val stationList: List<Station>
): SubRoute
