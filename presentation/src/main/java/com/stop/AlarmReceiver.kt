package com.stop

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val requestCode = intent.extras?.getInt("ALARM_REQUEST_CODE") ?: -1
        val alarmContent = intent.extras?.getString("ALARM_CONTENT") ?: ""

        val soundServiceIntent = Intent(context, SoundService::class.java)

        if (isMoreThanOreo()) {
            context.startForegroundService(soundServiceIntent)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            if (notificationManager.getNotificationChannel(ALARM_RECEIVER_CHANNEL_ID) == null) {
                NotificationChannel(ALARM_RECEIVER_CHANNEL_ID, ALARM_RECEIVER_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                    notificationManager.createNotificationChannel(this)
                }
            }

            val pendingIntent = PendingIntent.getActivity(context, requestCode, Intent(context, TestActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(context, ALARM_RECEIVER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_alarm_on_24)
                .setContentTitle(LAST_TRANSPORT_NOTIFICATION_TITLE)
                .setContentText(alarmContent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(pendingIntent, true)

            try {
                notificationManager.notify(ALARM_NOTIFICATION_ID, builder.build())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            context.startService(soundServiceIntent)

            Intent(context, TestActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(this)
            }
        }
    }

    companion object {
        const val ALARM_RECEIVER_CHANNEL_ID = "ALARM_RECEIVER_CHANNEL_ID"
        const val ALARM_RECEIVER_CHANNEL_NAME = "ALARM_RECEIVER_CHANNEL_NAME"
        const val ALARM_NOTIFICATION_ID = 123
        private const val LAST_TRANSPORT_NOTIFICATION_TITLE = "막차 알림"
    }

}