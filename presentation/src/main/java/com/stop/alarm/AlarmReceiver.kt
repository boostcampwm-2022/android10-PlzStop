package com.stop.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.stop.MainActivity
import com.stop.R
import com.stop.isMoreThanOreo
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_CODE
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_NOTIFICATION_HIGH_ID
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_NOTIFICATION_ID
import com.stop.util.getAlarmReceiverNotification
import com.stop.util.getAlarmReceiverPendingIntent
import com.stop.util.getAlarmScreenOnNotification
import com.stop.util.isScreenOn

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ALARM_NOTIFICATION_ID)

        val soundServiceIntent = Intent(context, SoundService::class.java)
        val content = context.getString(R.string.alarm_content_text)

        if (context.isScreenOn()) {
            if (isMoreThanOreo()) {
                context.startForegroundService(soundServiceIntent)
            } else {
                context.startService(soundServiceIntent)
            }

            val alarmStartPendingIntent = context.getAlarmReceiverPendingIntent()
            val alarmStartNotification = context.getAlarmScreenOnNotification(alarmStartPendingIntent, content)

            notificationManager.notify(ALARM_NOTIFICATION_ID, alarmStartNotification)

            /*Intent(context, MainActivity::class.java).apply {
                putExtra("ALARM_CODE", ALARM_CODE)
                context.startActivity(this)
            }*/
        } else {
            if (isMoreThanOreo()) {
                context.startForegroundService(soundServiceIntent)

                val alarmStartPendingIntent = context.getAlarmReceiverPendingIntent()
                val alarmStartNotification = context.getAlarmReceiverNotification(
                    alarmStartPendingIntent,
                    content
                )

                notificationManager.notify(ALARM_NOTIFICATION_HIGH_ID, alarmStartNotification)
            } else {
                context.startService(soundServiceIntent)
                Intent(context, MainActivity::class.java).apply {
                    putExtra("ALARM_CODE", ALARM_CODE)
                    context.startActivity(this)
                }
            }
        }

    }

}