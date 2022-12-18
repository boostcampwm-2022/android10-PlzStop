package com.stop.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun makeFullTime(time: String): Calendar {
    val currentTime = System.currentTimeMillis()
    val currentFormat = SimpleDateFormat("yyyy:MM:dd", Locale.getDefault())
    val currentDateTime = currentFormat.format(currentTime)
    val fullTime = "$currentDateTime:$time"

    val dateFormat = SimpleDateFormat("yyyy:MM:dd:HH:mm:ss", Locale.getDefault())
    val dateTime = try {
        dateFormat.parse(fullTime) as Date
    } catch (e: ParseException) {
        e.printStackTrace()
        Date()
    }

    val calendar = Calendar.getInstance()
    calendar.time = dateTime

    return calendar
}

fun convertTimeMillisToString(time: Long): String {
    val hours = (time / 1000) / 60 / 60 % 24
    val minutes = (time / 1000) / 60 % 60
    val seconds = (time / 1000) % 60

    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}