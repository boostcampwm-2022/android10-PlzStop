package com.stop.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.stop.AlarmActivity
import com.stop.R
import com.stop.isMoreThanOreo
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_CODE
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_NOTIFICATION_HIGH_ID
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_NOTIFICATION_ID
import com.stop.util.getAlarmReceiverNotification
import com.stop.util.getAlarmReceiverPendingIntent

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(ALARM_NOTIFICATION_ID)

        val content = context.getString(R.string.alarm_content_text)

        if (isMoreThanOreo()) {
            val soundRestartServiceIntent = Intent(context, SoundRestartService::class.java)
            context.startForegroundService(soundRestartServiceIntent)

            val alarmStartPendingIntent = context.getAlarmReceiverPendingIntent()
            val alarmStartNotification = context.getAlarmReceiverNotification(
                alarmStartPendingIntent,
                content
            )

            notificationManager.notify(ALARM_NOTIFICATION_HIGH_ID, alarmStartNotification)
        } else {
            val soundServiceIntent = Intent(context, SoundService::class.java)
            context.startService(soundServiceIntent)

            Intent(context, AlarmActivity::class.java).apply {
                putExtra("ALARM_CODE", ALARM_CODE)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(this)
            }
        }
    }

}