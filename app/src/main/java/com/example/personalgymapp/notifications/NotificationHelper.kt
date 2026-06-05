package com.example.personalgymapp.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.personalgymapp.R
import com.example.personalgymapp.model.TrainingSession
import java.text.SimpleDateFormat
import java.util.*

class NotificationHelper(private val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val CHANNEL_ID = "training_session_reminders"
        const val CHANNEL_NAME = "Training Reminders"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders for scheduled training sessions"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleSessionReminder(session: TrainingSession) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("sessionId", session.id)
            putExtra("clientName", session.clientName)
            putExtra("sessionType", session.sessionType)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            session.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance()
        val dateParts = session.date.split("-")
        if (dateParts.size == 3) {
            calendar.set(Calendar.YEAR, dateParts[0].toInt())
            calendar.set(Calendar.MONTH, dateParts[1].toInt() - 1)
            calendar.set(Calendar.DAY_OF_MONTH, dateParts[2].toInt())
        }

        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        try {
            val timeDate = timeFormat.parse(session.time)
            if (timeDate != null) {
                val timeCal = Calendar.getInstance()
                timeCal.time = timeDate
                calendar.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                calendar.set(Calendar.SECOND, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Schedule 15 minutes before the session
        val reminderTime = calendar.timeInMillis - (15 * 60 * 1000)
        
        if (reminderTime > System.currentTimeMillis()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent)
            }
        }
    }

    fun cancelSessionReminder(sessionId: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            sessionId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun showNotification(sessionId: Int, clientName: String, sessionType: String) {
        val deepLinkUri = Uri.parse("pgymapp://session/$sessionId")
        val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            sessionId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_logo) // Using existing app_logo
            .setContentTitle("Upcoming Session with $clientName")
            .setContentText("Your $sessionType session starts in 15 minutes!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(sessionId, notification)
    }
}
