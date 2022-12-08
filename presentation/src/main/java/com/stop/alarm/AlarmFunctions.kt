package com.stop.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.stop.isMoreThanSnow
import com.stop.makeFullTime

class AlarmFunctions(
    private val context: Context
) {

    private lateinit var pendingIntent: PendingIntent

    fun callAlarm(lastTime: String, alarmTime: Int, alarmCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val receiverIntent = Intent(context, AlarmReceiver::class.java)
        receiverIntent.apply {
            putExtra("ALARM_CODE", alarmCode)
        }

        val pendingIntent = if (isMoreThanSnow()) {
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

    fun cancelAlarm(alarmCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        pendingIntent = if (isMoreThanSnow()) {
            PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getBroadcast(context, alarmCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        alarmManager.cancel(pendingIntent)
    }

}