package com.stop.domain.model.route.seoul.subway

enum class TransportDirectionType {
    INNER, // 내선
    TO_FIRST, // 하행, 종점 출발
    OUTER, // 외선
    TO_END, // 상행, 기점 출발
    UNKNOWN // 계산하지 않은 경우
}