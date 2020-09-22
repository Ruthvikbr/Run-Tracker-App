package com.ruthvikbr.runtracker.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.ruthvikbr.runtracker.R
import com.ruthvikbr.runtracker.ui.MainActivity
import com.ruthvikbr.runtracker.utilities.Constants.ACTION_DISPLAY_TRACKING_FRAGMENT
import com.ruthvikbr.runtracker.utilities.Constants.ACTION_PAUSE_SERVICE
import com.ruthvikbr.runtracker.utilities.Constants.ACTION_START_OR_RESUME_SERVICE
import com.ruthvikbr.runtracker.utilities.Constants.ACTION_STOP_SERVICE
import com.ruthvikbr.runtracker.utilities.Constants.NOTIFICATIONS_CHANNEL_NAME
import com.ruthvikbr.runtracker.utilities.Constants.NOTIFICATION_CHANNEL_ID
import com.ruthvikbr.runtracker.utilities.Constants.NOTIFICATION_ID

class TrackingServices : LifecycleService() {

    var isFirstRun = true

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE -> {
                    if (isFirstRun){
                        startForegroundService()
                        isFirstRun = false
                    }
                }
                ACTION_PAUSE_SERVICE -> {

                }
                ACTION_STOP_SERVICE -> {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService(){
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentTitle("Tracking Your Run")
            .setContentText("00:00:00")
            .setContentIntent(createPendingIntent())

        startForeground(NOTIFICATION_ID,notificationBuilder.build())
    }

    private fun createPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this,MainActivity::class.java).also {
            it.action = ACTION_DISPLAY_TRACKING_FRAGMENT
        },
        FLAG_UPDATE_CURRENT

    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
            NOTIFICATIONS_CHANNEL_NAME,
        IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

}