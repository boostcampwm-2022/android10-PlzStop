package com.stop.alarm

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import com.stop.MainActivity
import com.stop.R
import com.stop.makeFullTime
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_CODE
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_MAP_CODE
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_NOTIFICATION_ID
import com.stop.util.getActivityPendingIntent
import com.stop.util.getAlarmDefaultNotification
import com.stop.util.getBroadcastPendingIntent

class AlarmFunctions(
    private val context: Context
) {
    fun callAlarm(lastTime: String, alarmTime: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val alarmSettingPendingIntent = context.getActivityPendingIntent(
            Intent(context, MainActivity::class.java).apply {
                putExtra("ALARM_MAP_CODE", ALARM_MAP_CODE)
            },
            ALARM_MAP_CODE
        )

        val content = context.getString(R.string.alarm_last_notification_text, lastTime, alarmTime.toString())
        val alarmSettingNotification = context.getAlarmDefaultNotification(
            alarmSettingPendingIntent,
            content
        )
        notificationManager.notify(ALARM_NOTIFICATION_ID, alarmSettingNotification)

        val alarmReceiverPendingIntent = context.getBroadcastPendingIntent(
            Intent(context, AlarmReceiver::class.java),
            ALARM_CODE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            makeFullTime(lastTime).timeInMillis - (alarmTime * 60 * 1000),
            alarmReceiverPendingIntent
        )
    }

    fun cancelAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmReceiverPendingIntent = context.getBroadcastPendingIntent(
            Intent(context, AlarmReceiver::class.java),
            ALARM_CODE
        )

        alarmManager.cancel(alarmReceiverPendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ALARM_NOTIFICATION_ID)
    }

}