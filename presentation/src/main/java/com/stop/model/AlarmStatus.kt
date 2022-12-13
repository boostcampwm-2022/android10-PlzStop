package com.stop.model

enum class AlarmStatus(val text: String) {
    NON_EXIST("현재 등록된 막차 알림이 없습니다"), EXIST("드래그하여 등록된 막차 알림을 확인할 수 있습니다"), MISSION("진행중인 미션이 있습니다")
}