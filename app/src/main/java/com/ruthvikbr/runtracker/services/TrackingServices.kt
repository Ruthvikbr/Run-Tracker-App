package com.ruthvikbr.runtracker.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.ruthvikbr.runtracker.R
import com.ruthvikbr.runtracker.ui.MainActivity
import com.ruthvikbr.runtracker.utilities.Constants.ACTION_DISPLAY_TRACKING_FRAGMENT
import com.ruthvikbr.runtracker.utilities.Constants.ACTION_PAUSE_SERVICE
import com.ruthvikbr.runtracker.utilities.Constants.ACTION_START_OR_RESUME_SERVICE
import com.ruthvikbr.runtracker.utilities.Constants.ACTION_STOP_SERVICE
import com.ruthvikbr.runtracker.utilities.Constants.FASTEST_UPDATE_INTERVAL
import com.ruthvikbr.runtracker.utilities.Constants.LOCATION_UPDATE_INTERVAL
import com.ruthvikbr.runtracker.utilities.Constants.NOTIFICATIONS_CHANNEL_NAME
import com.ruthvikbr.runtracker.utilities.Constants.NOTIFICATION_CHANNEL_ID
import com.ruthvikbr.runtracker.utilities.Constants.NOTIFICATION_ID
import com.ruthvikbr.runtracker.utilities.TrackingUtility

typealias polyline = MutableList<LatLng>
typealias polylines = MutableList<polyline>

class TrackingServices : LifecycleService() {

    var isFirstRun = true
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathToPoints = MutableLiveData<polylines>()
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, {
            updateLocation(it)
        })
    }

    private fun postInitialValues(){
        isTracking.postValue(false)
        pathToPoints.postValue(mutableListOf())
    }

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

    @SuppressLint("MissingPermission")
    private fun updateLocation(isTracking: Boolean){
        if(isTracking){
            if(TrackingUtility.hasLocationPermissions(this)){
                val request = LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_UPDATE_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if(isTracking.value!!){
                result?.locations?.let { locations->
                    for(location in locations){
                        addPathPoints(location)
                    }
                }
            }
        }
    }

    private fun addPathPoints(location: Location?){
        location?.let {
            val pos = LatLng(it.latitude,it.longitude)
            pathToPoints.value?.apply {
                last().add(pos)
                pathToPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPolyline() = pathToPoints.value?.apply {
        add(mutableListOf())
        pathToPoints.postValue(this)
    } ?: pathToPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForegroundService(){
        addEmptyPolyline()
        isTracking.postValue(true)

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