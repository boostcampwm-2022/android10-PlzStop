package com.stop.model.route

data class WalkSubRoute(
    override val distance: Double,
    override val estimatedTime: Int,
    override val moveType: MoveType,
    override val startPlace: Place,
    override val endPlace: Place,
    val movePathCoordinates: List<WalkInfo> // steps 이동 경로 정보
): SubRoute
