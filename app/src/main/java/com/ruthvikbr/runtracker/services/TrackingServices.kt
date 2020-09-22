package com.ruthvikbr.runtracker.services

import android.content.Intent
import androidx.lifecycle.LifecycleService

class TrackingServices : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                "ACTION_START_OR_RESUME_SERVICE" -> {

                }
                "ACTION_PAUSE_SERVICE" -> {

                }
                "ACTION_STOP_SERVICE" -> {

                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

}