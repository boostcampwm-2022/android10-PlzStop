package com.stop.ui.util

import com.stop.domain.model.route.tmap.custom.Itinerary
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
    fun getInformationString(itinerary: Itinerary): String {
        val informationString = StringBuilder()

        informationString.append("${getDistanceString(itinerary.totalDistance)}    ")
        informationString.append("도보 ${getTimeString(itinerary.walkTime)}    ")
        informationString.append(
            "${DecimalFormat("#,###").format(itinerary.totalFare.dropLast(0).toInt())}원    "
        )
        informationString.append("환승 ${itinerary.transferCount}회")

        return informationString.toString()
    }

    private fun getDistanceString(distance: Double): String {
        return if (distance >= 1000) {
            "${(distance / 1000).toInt()}km"
        } else {
            "${distance.toInt()}m"
        }
    }
}