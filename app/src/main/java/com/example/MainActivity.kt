package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.billing.RevenueCatManager
import com.example.data.IronVisionDatabase
import com.example.data.WorkoutRepository
import com.example.notifications.WorkoutReminderWorker
import com.example.ui.navigation.IronVisionNavGraph
import com.example.ui.theme.IronDarkBackground
import com.example.ui.theme.IronVisionTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private lateinit var database: IronVisionDatabase
    private lateinit var repository: WorkoutRepository
    private lateinit var revenueCatManager: RevenueCatManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = IronVisionDatabase.getDatabase(this)
        repository = WorkoutRepository(database.workoutDao())
        revenueCatManager = RevenueCatManager.getInstance(this)

        // Seed realistic workout history if first run
        lifecycleScope.launch(Dispatchers.IO) {
            repository.seedSampleDataIfEmpty()
        }

        // Setup background workout reminders
        setupWorkoutReminders()

        // Request notification permissions for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        val hasCameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        setContent {
            IronVisionTheme {
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = IronDarkBackground
                ) {
                    IronVisionNavGraph(
                        navController = navController,
                        repository = repository,
                        revenueCatManager = revenueCatManager,
                        hasCameraPermission = hasCameraPermission
                    )
                }
            }
        }
    }

    private fun setupWorkoutReminders() {
        val reminderWorkRequest = PeriodicWorkRequestBuilder<WorkoutReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(2, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyWorkoutReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            reminderWorkRequest
        )
    }
}

