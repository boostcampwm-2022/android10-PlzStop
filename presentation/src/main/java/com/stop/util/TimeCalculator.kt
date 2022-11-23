package com.stop.util

object TimeCalculator {

    fun secondToHourAndMinute(second: Int): String {
        return "${second / 60 / 60}시간 ${second / 60 % 60}분"
    }
}