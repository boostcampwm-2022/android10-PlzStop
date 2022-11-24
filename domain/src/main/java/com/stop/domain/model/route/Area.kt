package com.stop.domain.model.route

enum class Area(val cityName: String) {
    SEOUL("서울특별시"), GYEONGGI("경기도"), UN_SUPPORT_AREA("Unknown");

    companion object {
        fun getAreaByName(name: String): Area {
            return values().firstOrNull {
                it.cityName == name
            } ?: UN_SUPPORT_AREA
        }
    }
}