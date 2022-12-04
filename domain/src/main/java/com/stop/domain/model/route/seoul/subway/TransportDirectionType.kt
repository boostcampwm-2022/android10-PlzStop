package com.stop.domain.model.route.seoul.subway

enum class TransportDirectionType(val divisionValue: String) {
    INNER("1"), // 내선, 상행, 기점 출발
    OUTER("2"), // 외선, 하행, 종점 출발
    UNKNOWN("-1") // 계산하지 않은 경우
}