package com.stop.ui.mission

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.stop.R
import com.stop.domain.usecase.nearplace.GetNearPlacesUseCase
import com.stop.isMoreThanOreo
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_TIME
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.LAST_TIME
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class MissionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())

        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val id = applicationContext.getString(R.string.mission_notification_channel_id)
        val title = applicationContext.getString(R.string.mission_notification_title)
        val cancel = applicationContext.getString(R.string.alarm_cancel_text)

        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        createChannel(id)

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(NOTIFICATION_CONTENT)
            .setSmallIcon(R.mipmap.ic_bus)
            .setOngoing(true) // 사용자가 지우지 못하도록 막음
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    private fun createChannel(id: String) {
        if (isMoreThanOreo()) {
            if (notificationManager.getNotificationChannel(id) == null) {
                val name = applicationContext.getString(R.string.notification_channel_name)
                NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                    notificationManager.createNotificationChannel(this)
                }
            }
        }
    }



    companion object {
        const val NOTIFICATION_ID = 12
        private const val NOTIFICATION_CONTENT = "사용자의 위치를 추적중입니다."
    }
}