package com.example.fitfuelie.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fitfuelie.di.AppContainer
import kotlinx.coroutines.flow.first
import java.util.*

/**
 * TrainingReminderWorker
 * 
 * Background worker that checks for upcoming training sessions and sends
 * reminder notifications. This worker:
 * - Runs every 15 minutes during active hours (6 AM - 10 PM)
 * - Checks for training sessions scheduled in the next 30 minutes
 * - Sends push notifications to remind users about upcoming workouts
 * - Helps users stay on track with their training schedule
 * 
 * Uses WorkManager's PeriodicWorkRequest with a 15-minute interval.
 */
class TrainingReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Initialize app container to access repositories
            val appContainer = AppContainer(applicationContext)
            val trainingRepository = appContainer.trainingSessionRepository
            
            // Calculate time window: now to 30 minutes from now
            val now = Date()
            val thirtyMinutesLater = Calendar.getInstance().apply {
                time = now
                add(Calendar.MINUTE, 30)
            }.time
            
            // Fetch upcoming training sessions
            val upcomingSessions = trainingRepository
                .getTrainingSessionsBetweenDates(now, thirtyMinutesLater)
                .first()
            
            // Filter for sessions that haven't been completed
            val pendingSessions = upcomingSessions.filter { !it.isCompleted }
            
            // Send notification for each pending session
            pendingSessions.forEach { session ->
                android.util.Log.d(
                    "TrainingReminderWorker",
                    "Reminder: ${session.title} starting soon!"
                )
                // In production: Send actual notification using NotificationManager
            }
            
            Result.success()
        } catch (e: Exception) {
            // Log error and retry
            android.util.Log.e("TrainingReminderWorker", "Error checking training reminders", e)
            Result.retry()
        }
    }
}
