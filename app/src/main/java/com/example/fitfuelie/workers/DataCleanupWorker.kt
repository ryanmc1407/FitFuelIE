package com.example.fitfuelie.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.fitfuelie.di.AppContainer
import kotlinx.coroutines.flow.first
import java.util.*

/**
 * DataCleanupWorker
 * 
 * Background worker that performs periodic database maintenance and cleanup.
 * This worker:
 * - Runs once per week (every 7 days)
 * - Removes completed grocery items older than 30 days
 * - Archives old meal and training data (optional)
 * - Optimizes database performance by cleaning up stale data
 * - Helps keep the app's storage footprint manageable
 * 
 * Scheduled using WorkManager's PeriodicWorkRequest with a 7-day interval.
 */
class DataCleanupWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Initialize app container to access repositories
            val appContainer = AppContainer(applicationContext)
            val groceryRepository = appContainer.groceryItemRepository
            
            // Calculate cutoff date: 30 days ago
            val thirtyDaysAgo = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_MONTH, -30)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
            
            // Fetch all grocery items
            val allGroceryItems = groceryRepository.getAllGroceryItems().first()
            
            // Filter and delete old completed items
            var deletedCount = 0
            allGroceryItems.forEach { item ->
                // If item is purchased and older than 30 days, delete it
                if (item.isPurchased && item.id < thirtyDaysAgo.time) {
                    groceryRepository.deleteGroceryItem(item)
                    deletedCount++
                }
            }
            
            // Log cleanup results
            android.util.Log.d(
                "DataCleanupWorker",
                "Cleanup completed: Deleted $deletedCount old grocery items"
            )
            
            // In production: Could also archive old meals/training sessions
            // to a separate table or export to cloud storage
            
            Result.success()
        } catch (e: Exception) {
            // Log error and retry
            android.util.Log.e("DataCleanupWorker", "Error during data cleanup", e)
            Result.retry()
        }
    }
}
