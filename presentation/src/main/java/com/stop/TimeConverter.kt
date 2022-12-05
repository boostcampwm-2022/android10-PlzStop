package com.stop

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun makeFullTime(time: String): Calendar {
    val currentTime = System.currentTimeMillis()
    val currentFormat = SimpleDateFormat("yyyy:MM:dd", Locale.getDefault())
    val currentDateTime = currentFormat.format(currentTime)
    val fullTime = "$currentDateTime:$time"


    val dateFormat = SimpleDateFormat("yyyy:MM:dd:HH:mm:ss", Locale.getDefault())
    var dateTime = Date()
    try {
        dateTime = dateFormat.parse(fullTime) as Date
    } catch (e: ParseException) {
        e.printStackTrace()
    }

    val calendar = Calendar.getInstance()
    calendar.time = dateTime

    return calendar
}