package com.stop.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.PowerManager
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.core.app.NotificationCompat
import com.stop.MainActivity
import com.stop.R
import com.stop.alarm.AlarmReceiver
import com.stop.isMOreThanRedVelVet
import com.stop.isMoreThanOreo
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_CODE
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_MAP_CODE

fun Context.getAlarmSettingPendingIntent(): PendingIntent {
    val intent = Intent(this, MainActivity::class.java)
    intent.putExtra("ALARM_MAP_CODE", ALARM_MAP_CODE)
    return PendingIntent.getActivity(
        this,
        ALARM_MAP_CODE,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

fun Context.getAlarmSettingNotification(pendingIntent: PendingIntent, content: String): Notification {
    val id = applicationContext.getString(R.string.notification_channel_id)
    val name = applicationContext.getString(R.string.notification_channel_name)
    val title = applicationContext.getString(R.string.notification_title)

    createDefaultNotificationChannel(applicationContext, id, name)

    return NotificationCompat.Builder(applicationContext, id)
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(R.mipmap.ic_bus)
        .setOngoing(true)
        .setContentIntent(pendingIntent)
        .build()
}

fun Context.getAlarmReceiverPendingIntent(): PendingIntent {
    val intent = Intent(this, AlarmReceiver::class.java)
    intent.putExtra("ALARM_CODE", ALARM_CODE)
    return PendingIntent.getBroadcast(
        this,
        ALARM_CODE,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}

fun Context.isScreenOn() =
    (getSystemService(Context.POWER_SERVICE) as PowerManager).isInteractive

fun Context.getAlarmReceiverNotification(pendingIntent: PendingIntent, content: String): Notification {
    val id = applicationContext.getString(R.string.notification_channel_id)
    val name = applicationContext.getString(R.string.notification_channel_name)
    val title = applicationContext.getString(R.string.notification_title)

    createHighNotificationChannel(applicationContext, id, name)

    return NotificationCompat.Builder(applicationContext, id)
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(R.mipmap.ic_bus)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setFullScreenIntent(pendingIntent, true)
        .build()
}

private fun createDefaultNotificationChannel(context: Context, id: String, name: String) {
    if (isMoreThanOreo()) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        NotificationChannel(
            id,
            name,
            importance
        ).apply {
            notificationManager.createNotificationChannel(this)
        }
    }
}

private fun createHighNotificationChannel(context: Context, id: String, name: String) {
    if (isMoreThanOreo()) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val importance = NotificationManager.IMPORTANCE_HIGH

        NotificationChannel(
            id,
            name,
            importance
        ).apply {
            notificationManager.createNotificationChannel(this)
        }
    }
}

fun Context.getScreenSize(): Size {
    return if (isMOreThanRedVelVet()) {
        val metrics: WindowMetrics = getSystemService(WindowManager::class.java).currentWindowMetrics
        Size(metrics.bounds.width(), metrics.bounds.height())
    } else {
        val display = getSystemService(WindowManager::class.java).defaultDisplay
        val metrics = if (display != null) {
            DisplayMetrics().also { display.getRealMetrics(it) }
        } else {
            Resources.getSystem().displayMetrics
        }
        Size(metrics.widthPixels, metrics.heightPixels)
    }
}
