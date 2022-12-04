package com.stop

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.stop.domain.usecase.alarm.DeleteAlarmUseCase
import com.stop.domain.usecase.alarm.GetAlarmUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SoundService : LifecycleService() {

    @Inject
    lateinit var getAlarmUseCase: GetAlarmUseCase
    @Inject
    lateinit var deleteAlarmUseCase: DeleteAlarmUseCase

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var vibratorManager: VibratorManager? = null

    override fun onCreate() {
        super.onCreate()

        lifecycleScope.launch {
            getAlarmUseCase.getAlarm().collectLatest { alarmData ->
                alarmData?.let {
                    if (it.alarmMethod) {
                        mediaPlayer = MediaPlayer.create(this@SoundService, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)).apply {
                            setWakeMode(this@SoundService, PowerManager.PARTIAL_WAKE_LOCK)
                            isLooping = true
                            start()
                        }
                        return@let
                    }

                    val pattern = longArrayOf(100, 200, 100, 200, 100, 200)
                    val amplitude = intArrayOf(0, 50, 0, 100, 0, 200)
                    if (isUnderOreo()) {
                        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        vibrator?.vibrate(pattern, 0)
                    } else if (isMoreThanOreoUnderRedVelVet()) {
                        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                        val effect = VibrationEffect.createWaveform(pattern, amplitude, 0)
                        vibrator?.vibrate(effect)
                    } else if (isMoreThanSnow()) {
                        vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                        val effect = VibrationEffect.createWaveform(pattern, amplitude, 0)
                        val combinedVibration = CombinedVibration.createParallel(effect)
                        vibratorManager?.vibrate(combinedVibration)
                    }
                }
            }

            deleteAlarmUseCase.deleteAlarm()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        createNotification()
        stopForeground(true)


        return START_STICKY
    }

    private fun createNotification() {
        val notificationManager = this.getSystemService(NotificationManager::class.java)
        if (isMoreThanOreo()) {
            if (notificationManager.getNotificationChannel(DEFAULT_NOTIFICATION_CHANNEL_ID) == null) {
                NotificationChannel(DEFAULT_NOTIFICATION_CHANNEL_ID, DEFAULT_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
                    notificationManager.createNotificationChannel(this)
                }
            }
        }

        val builder = NotificationCompat.Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_alarm_on_24)
            .setContentTitle(null)
            .setContentText(null)

        startForeground(DEFAULT_NOTIFICATION_ID, builder.build())
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer = null

        vibrator?.cancel()
        vibrator = null

        if (isMoreThanSnow()) {
            vibratorManager?.cancel()
        }
        vibratorManager = null

        super.onDestroy()
    }

    companion object {
        private const val DEFAULT_NOTIFICATION_CHANNEL_ID = "DEFAULT_NOTIFICATION_CHANNEL_ID"
        private const val DEFAULT_NOTIFICATION_CHANNEL_NAME = "DEFAULT_NOTIFICATION_CHANNEL_NAME"
        private const val DEFAULT_NOTIFICATION_ID = 124
    }

}