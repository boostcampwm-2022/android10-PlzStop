package com.stop.ui.util

import com.stop.domain.model.route.seoul.subway.StationLastTime
import com.stop.domain.model.route.tmap.custom.Itinerary
import com.stop.model.route.RouteItem
import java.text.DecimalFormat

object DrawerStringUtils {
    @JvmStatic
    fun getTimeString(time: Int): String {
        val timeString = StringBuilder()
        val hour = time / 3600

        if (hour != 0) {
            timeString.append("${hour}시간 ")
        }

        return timeString.append("${(time % 3600) / 60}분").toString()
    }

    @JvmStatic
    fun getTopInformationString(itinerary: Itinerary): String {
        val topInformationString = StringBuilder()

        topInformationString.append("${getDistanceString(itinerary.totalDistance)}    ")
        topInformationString.append("도보 ${getTimeString(itinerary.walkTime)}    ")
        topInformationString.append(
            "${DecimalFormat("#,###").format(itinerary.totalFare.dropLast(0).toInt())}원    "
        )
        topInformationString.append("환승 ${itinerary.transferCount}회")

        return topInformationString.toString()
    }

    @JvmStatic
    fun getRouteItemInformationString(routeItem: RouteItem): String {
        val routeItemInformationString = StringBuilder()

        routeItemInformationString.append("${getDistanceString(routeItem.distance)}    ")
        routeItemInformationString.append(getTimeString(routeItem.travelTime))

        return routeItemInformationString.toString()
    }

    @JvmStatic
    fun getLastTimeString(lastTime: String?): String {
        return if (lastTime != null) {
            "막차 $lastTime"
        } else {
            ""
        }
    }

    private fun getDistanceString(distance: Double): String {
        return if (distance >= 1000) {
            "${(distance / 1000).toInt()}km"
        } else {
            "${distance.toInt()}m"
        }
    }
}