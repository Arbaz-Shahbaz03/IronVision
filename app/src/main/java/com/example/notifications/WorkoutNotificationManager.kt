package com.example.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class WorkoutNotificationManager(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannels()
    }

    private fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val remindersChannel = NotificationChannel(
                CHANNEL_ID_REMINDERS,
                "Workout Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily reminders to hit your workout goals."
            }

            val winterArcChannel = NotificationChannel(
                CHANNEL_ID_WINTER_ARC,
                "Winter Arc Updates",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Special notifications for the Winter Arc program."
            }

            notificationManager.createNotificationChannel(remindersChannel)
            notificationManager.createNotificationChannel(winterArcChannel)
        }
    }

    fun showReminderNotification(title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_REMINDERS)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Fallback icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        
        try {
            notificationManager.notify(NOTIFICATION_ID_REMINDER, notification)
        } catch (e: SecurityException) {
            // Ignore if permission was revoked between check and notify
        }
    }

    fun showWinterArcNotification(title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_WINTER_ARC)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ID_WINTER_ARC, notification)
        } catch (e: SecurityException) {
            // Ignore if permission was revoked
        }
    }

    companion object {
        const val CHANNEL_ID_REMINDERS = "workout_reminders"
        const val CHANNEL_ID_WINTER_ARC = "winter_arc_updates"
        const val NOTIFICATION_ID_REMINDER = 1001
        const val NOTIFICATION_ID_WINTER_ARC = 1002
    }
}
