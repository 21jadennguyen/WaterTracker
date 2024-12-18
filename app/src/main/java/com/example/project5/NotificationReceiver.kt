package com.example.project5

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification()  // Trigger the notification
    }
}
