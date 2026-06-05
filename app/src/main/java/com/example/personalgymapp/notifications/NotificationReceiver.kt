package com.example.personalgymapp.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sessionId = intent.getIntExtra("sessionId", -1)
        val clientName = intent.getStringExtra("clientName") ?: "Client"
        val sessionType = intent.getStringExtra("sessionType") ?: "Training"

        if (sessionId != -1) {
            val notificationHelper = NotificationHelper(context)
            notificationHelper.showNotification(sessionId, clientName, sessionType)
        }
    }
}
