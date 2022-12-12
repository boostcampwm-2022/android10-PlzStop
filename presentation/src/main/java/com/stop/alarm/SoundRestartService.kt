package com.stop.alarm

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.stop.AlarmActivity
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_CODE
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_NOTIFICATION_ID
import com.stop.util.getActivityPendingIntent
import com.stop.util.getAlarmDefaultNotification

class SoundRestartService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notImportantPendingIntent = this.getActivityPendingIntent(
            Intent(this, AlarmActivity::class.java),
            ALARM_CODE
        )
        val notImportantNotification = this.getAlarmDefaultNotification(
            notImportantPendingIntent,
            ""
        )
        startForeground(ALARM_NOTIFICATION_ID, notImportantNotification)

        val serviceIntent = Intent(this, SoundService::class.java)
        startService(serviceIntent)

        stopForeground(true)
        stopSelf()

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}