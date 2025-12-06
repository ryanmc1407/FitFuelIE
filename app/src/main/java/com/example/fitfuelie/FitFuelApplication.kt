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
 * Custom Application class for the FitFuel app.
 * Responsible for initializing app-wide components and services:
 * - WorkManager: Schedules and manages background tasks
 * - Periodic workers for nutrition tracking, training reminders, and data cleanup
 * 
 * This class is registered in AndroidManifest.xml and created when the app starts.
 */
class FitFuelApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize WorkManager and schedule all periodic tasks
        scheduleBackgroundTasks()
    }

    /**
     * Schedules all periodic background tasks using WorkManager.
     * 
     * Three main workers are scheduled:
     * 1. DailyNutritionSummaryWorker - Runs daily at midnight
     * 2. TrainingReminderWorker - Runs every 15 minutes during active hours
     * 3. DataCleanupWorker - Runs weekly for database maintenance
     * 
     * All workers use KEEP policy to preserve existing schedules across app updates.
     */
    private fun scheduleBackgroundTasks() {
        val workManager = WorkManager.getInstance(this)

        // 1. Daily Nutrition Summary Worker
        // Runs once per day to calculate previous day's nutrition totals
        val dailyNutritionRequest = PeriodicWorkRequestBuilder<DailyNutritionSummaryWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true) // Only run when battery is not low
                    .build()
            )
            .build()

        // Enqueue with KEEP policy - won't replace if already scheduled
        workManager.enqueueUniquePeriodicWork(
            "daily_nutrition_summary",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyNutritionRequest
        )

        // 2. Training Reminder Worker
        // Runs every 15 minutes to check for upcoming training sessions
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
        // Runs once per week to clean up old data
        val dataCleanupRequest = PeriodicWorkRequestBuilder<DataCleanupWorker>(
            repeatInterval = 7,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .setRequiresDeviceIdle(true) // Only run when device is idle
                    .build()
            )
            .build()

        workManager.enqueueUniquePeriodicWork(
            "data_cleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            dataCleanupRequest
        )

        // 4. Daily Reminder Worker
        // Runs daily to remind user to log data
        val dailyReminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true) // Don't run if battery is low
                    .build()
            )
            .setInitialDelay(12, TimeUnit.HOURS) // Start around noon/evening if app opened in morning
            .build()

        // I'm using KEEP here so that if the work is already scheduled, it doesn't get replaced.
        // This prevents duplicate tasks from piling up.
        workManager.enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyReminderRequest
        )
    }
}
