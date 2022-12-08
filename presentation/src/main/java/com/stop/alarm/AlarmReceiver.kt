package com.stop.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.stop.alarm.LastTimeCheckWorker.Companion.NOTIFICATION_ID
import com.stop.MainActivity
import com.stop.R
import com.stop.isMoreThanOreo

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val alarmCode = intent.extras?.getInt("ALARM_CODE") ?: -1

        val soundServiceIntent = Intent(context, SoundService::class.java)

        if (isMoreThanOreo()) {
            context.startForegroundService(soundServiceIntent)

            val id = context.getString(R.string.notification_channel_id)
            val name = context.getString(R.string.notification_channel_name)

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            if (notificationManager.getNotificationChannel(id) == null) {
                NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
                    notificationManager.createNotificationChannel(this)
                }
            }

            val pendingIntent = PendingIntent.getActivity(context, alarmCode, Intent(context, MainActivity::class.java).apply {
                putExtra("ALARM_CODE", alarmCode)
            }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val title = context.getString(R.string.notification_title)
            val content = context.getString(R.string.alarm_content_text)

            val builder = NotificationCompat.Builder(context, id)
                .setSmallIcon(R.mipmap.ic_bus)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(pendingIntent, true)

            try {
                notificationManager.notify(NOTIFICATION_ID, builder.build())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            context.startService(soundServiceIntent)

            Intent(context, MainActivity::class.java).apply {
                putExtra("ALARM_CODE", alarmCode)
                context.startActivity(this)
            }
        }
    }

}