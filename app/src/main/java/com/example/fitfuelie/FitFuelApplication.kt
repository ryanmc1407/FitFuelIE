package com.example.fitfuelie

import android.app.Application
import androidx.work.*
import com.example.fitfuelie.workers.DailyReminderWorker
import com.example.fitfuelie.workers.DataCleanupWorker
import com.example.fitfuelie.workers.DailyNutritionSummaryWorker
import com.example.fitfuelie.workers.TrainingReminderWorker
import java.util.concurrent.TimeUnit

/**
 * FitFuelApplication
 * 
 * This is a custom Application class that runs when the app starts.
 * It's like MainActivity, but for app-wide setup instead of UI setup.
 * 
 * What does it do?
 * - Sets up background tasks (workers) that run even when the app is closed
 * - Initializes services that the whole app needs
 * 
 * This class is registered in AndroidManifest.xml , Android knows to create it
 * when the app launches.
 * 
 * Why use Application class?
 * - onCreate() runs before any Activity is created
 * - Perfect for setting up things that need to run once for the whole app
 * - Like scheduling background tasks, initializing analytics, etc.
 */
class FitFuelApplication : Application() {

    /**
     * Called when the app is first created (before any Activity)
     * This is where I set up app-wide services
     */
    override fun onCreate() {
        super.onCreate()
        
        // Schedule all background workers
        // These will run periodically even if the user closes the app
        scheduleBackgroundTasks()
    }

    /**
     * Sets up all the background workers using WorkManager
     * 
     * WorkManager is Android's  way to do background work.
     * it handles battery optimization, network conditions.
     * 
     * I schedule 4 different workers:
     * 1. Daily nutrition summary - calculates daily totals
     * 2. Training reminders - reminds user about upcoming workouts
     * 3. Data cleanup - removes old data to save space
     * 4. Daily reminders - reminds user to log meals
     */
    private fun scheduleBackgroundTasks() {
        val workManager = WorkManager.getInstance(this)

        // 1. Daily Nutrition Summary Worker
        // I want this to run once a day to add up all the calories.
        val dailyNutritionRequest = PeriodicWorkRequestBuilder<DailyNutritionSummaryWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true) // I check if the battery is okay first so I don't drain it.
                    .build()
            )
            .build()

        // I use 'enqueueUniquePeriodicWork' to make sure I don't accidentally schedule the same task twice.
        // 'KEEP' means if it's already there, I keep the old one.
        workManager.enqueueUniquePeriodicWork(
            "daily_nutrition_summary",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyNutritionRequest
        )

        // 2. Training Reminder Worker
        // This one checks every 15 minutes to see if you have a workout coming up.
        val trainingReminderRequest = PeriodicWorkRequestBuilder<TrainingReminderWorker>(
            repeatInterval = 15,
            repeatIntervalTimeUnit = TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "training_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            trainingReminderRequest
        )

        // 3. Data Cleanup Worker
        // This runs once a week to delete old data I don't need anymore.
        val dataCleanupRequest = PeriodicWorkRequestBuilder<DataCleanupWorker>(
            repeatInterval = 7,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiresDeviceIdle(true) // I wait until the user isn't using the phone so I don't slow it down.
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "data_cleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            dataCleanupRequest
        )

        // 4. Daily Reminder Worker
        // This reminds the user to log their food if they haven't.
        val dailyReminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true) // Save battery!
                    .build()
            )
            .setInitialDelay(12, TimeUnit.HOURS) // Wait 12 hours so the notification doesn't pop up in the middle of the night.
            .build()

        // Again, using KEEP to avoid duplicates.
        workManager.enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyReminderRequest
        )
    }
}
