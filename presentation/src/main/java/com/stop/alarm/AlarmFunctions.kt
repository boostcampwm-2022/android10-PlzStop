package com.stop.alarm

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import com.stop.makeFullTime
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_NOTIFICATION_ID
import com.stop.util.getAlarmReceiverPendingIntent
import com.stop.util.getAlarmSettingNotification
import com.stop.util.getAlarmSettingPendingIntent

class AlarmFunctions(
    private val context: Context
) {
    fun callAlarm(lastTime: String, alarmTime: Int) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val alarmSettingPendingIntent = context.getAlarmSettingPendingIntent()
        val alarmSettingNotification = context.getAlarmSettingNotification(
            alarmSettingPendingIntent,
            "알람이 막차시간 ${lastTime}에서 ${alarmTime}전에 울릴예정입니다."
        )
        notificationManager.notify(ALARM_NOTIFICATION_ID, alarmSettingNotification)

        val alarmReceiverPendingIntent = context.getAlarmReceiverPendingIntent()

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            makeFullTime(lastTime).timeInMillis - (alarmTime * 60 * 1000),
            alarmReceiverPendingIntent
        )
    }

    fun cancelAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmReceiverPendingIntent = context.getAlarmReceiverPendingIntent()

        alarmManager.cancel(alarmReceiverPendingIntent)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ALARM_NOTIFICATION_ID)
    }

}