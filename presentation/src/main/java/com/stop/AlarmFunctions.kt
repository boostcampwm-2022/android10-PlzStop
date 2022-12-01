package com.stop

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AlarmFunctions(
    private val context: Context
) {

    private lateinit var pendingIntent: PendingIntent

    fun callAlarm(lastTime: String, alarmTime: Int, alarmCode: Int, alarmContent: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val receiverIntent = Intent(context, AlarmReceiver::class.java)
        receiverIntent.apply {
            putExtra("ALARM_REQUEST_CODE", alarmCode)
            putExtra("ALARM_CONTENT", alarmContent)
        }

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context, alarmCode, receiverIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getBroadcast(context, alarmCode, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            makeFullTime(lastTime).timeInMillis - (alarmTime * 60 * 1000),
            pendingIntent
        )
    }

    private fun makeFullTime(lastTime: String): Calendar {
        val currentTime = System.currentTimeMillis()
        val currentFormat = SimpleDateFormat("yyyy:MM:dd", Locale.getDefault())
        val currentDateTime = currentFormat.format(currentTime)
        val fullTime = "$currentDateTime:$lastTime"


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

    fun cancelAlarm(alarmCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarmManager.cancel(pendingIntent)
    }

}