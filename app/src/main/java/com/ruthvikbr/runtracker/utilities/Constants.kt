package com.ruthvikbr.runtracker.utilities

import android.graphics.Color

object Constants {

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_DISPLAY_TRACKING_FRAGMENT = "ACTION_DISPLAY_TRACKING_FRAGMENT"

    const val FASTEST_UPDATE_INTERVAL = 2000L
    const val LOCATION_UPDATE_INTERVAL = 5000L

    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 8f

    const val MAP_ZOOM = 15f

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATIONS_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1
}