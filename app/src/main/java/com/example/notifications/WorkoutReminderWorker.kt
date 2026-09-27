package com.example.notifications

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.Calendar

class WorkoutReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val notificationManager = WorkoutNotificationManager(context)

        // Simple logic to determine which notification to show based on time/day
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        if (hour in 17..19) {
            notificationManager.showReminderNotification(
                "Time to crush your workout! \ud83d\udcaa",
                "Don't skip your evening session. Your goals are waiting."
            )
        } else if (dayOfWeek == Calendar.MONDAY && hour in 7..9) {
            notificationManager.showWinterArcNotification(
                "Your Winter Arc workout is waiting. \u2744\ufe0f",
                "Start the week strong! Check today's Push day plan."
            )
        } else if (hour > 20) {
            notificationManager.showReminderNotification(
                "Missed a session?",
                "You missed today's workout. Get back on track tomorrow!"
            )
        } else {
            // Default generic reminder
            notificationManager.showReminderNotification(
                "IronVision Form Coach",
                "Ready to lift? Perfect your form with live AI tracking."
            )
        }

        return Result.success()
    }
}
