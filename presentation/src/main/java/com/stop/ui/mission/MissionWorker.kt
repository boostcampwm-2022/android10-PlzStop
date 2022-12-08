package com.stop.ui.mission

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.android.gms.location.*
import com.stop.MainActivity
import com.stop.R
import com.stop.isMoreThanOreo
import com.stop.model.Location
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class MissionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val missionManager: MissionManager
) : CoroutineWorker(context, workerParameters) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL_UNIT).build()
    private lateinit var locationCallback: LocationCallback

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())
        getPersonLocation()
        test()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        return Result.success()
    }

    private fun getPersonLocation() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        missionManager.userLocation.value = Location(location.latitude, location.longitude)
                    }
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private suspend fun test() {
        while (NUM < 60) {
            Log.d("MissionWorker", "찍히나 테스트 ${NUM}")
            NUM += 1
            delay(5000)
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val id = applicationContext.getString(R.string.mission_notification_channel_id)
        val title = applicationContext.getString(R.string.mission_notification_title)

        createChannel(id)

        val pendingIntent = PendingIntent.getActivity(applicationContext, MISSION_CODE, Intent(applicationContext, MainActivity::class.java).apply {
            putExtra("MISSION_CODE", MISSION_CODE)
        }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(NOTIFICATION_CONTENT)
            .setSmallIcon(R.mipmap.ic_bus)
            .setOngoing(true) // 사용자가 지우지 못하도록 막음
            .setContentIntent(pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ForegroundInfo(NOTIFICATION_ID, notification, FOREGROUND_SERVICE_TYPE_LOCATION)
        }

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    private fun createChannel(id: String) {
        if (isMoreThanOreo()) {
            Log.d("MissionWorker", "createChannel ${notificationManager.getNotificationChannel(id)}")
            if (notificationManager.getNotificationChannel(id) == null) {
                val name = applicationContext.getString(R.string.mission_notification_channel_name)
                NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                    notificationManager.createNotificationChannel(this)
                }
            }
        }
    }

    companion object {
        const val NOTIFICATION_ID = 82
        private const val NOTIFICATION_CONTENT = "사용자의 위치를 추적중입니다."
        private var NUM = 0
        private const val INTERVAL_UNIT = 1000L
        const val MISSION_CODE = 88
    }

}