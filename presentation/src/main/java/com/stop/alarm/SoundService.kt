package com.stop.alarm

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.*
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.stop.domain.usecase.alarm.GetAlarmUseCase
import com.stop.isMoreThanOreoUnderRedVelVet
import com.stop.isMoreThanSnow
import com.stop.isUnderOreo
import com.stop.ui.alarmsetting.AlarmSettingFragment.Companion.ALARM_CODE
import com.stop.util.getBroadcastPendingIntent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SoundService : LifecycleService() {

    @Inject
    lateinit var getAlarmUseCase: GetAlarmUseCase

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null
    private var vibratorManager: VibratorManager? = null

    override fun onCreate() {
        super.onCreate()
        normalExit = false

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
        }


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        return START_STICKY
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

        if (!normalExit) {
            setAlarmTimer()
        }

        super.onDestroy()
    }

    private fun setAlarmTimer() {
        val restartPendingIntent = this.getBroadcastPendingIntent(
            Intent(this, AlarmReceiver::class.java),
            ALARM_CODE
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            restartPendingIntent
        )
    }

    companion object {
        var normalExit: Boolean = false
    }

}