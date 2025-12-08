package com.example.fitfuelie.data.local.dao

import androidx.room.*
import com.example.fitfuelie.data.local.entity.TrainingSession
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * TrainingSessionDao
 * 
 * Handles all database operations for training sessions .
 * This lets me store workout plans and track completed sessions.
 * 
 * I added functions to count completed sessions and calculate total training time
 * because these are useful for the dashboard statistics.
 */
@Dao
interface TrainingSessionDao {

    /**
     * Gets all training sessions, newest first
     * Returns Flow so the calendar updates automatically when sessions are added
     */
    @Query("SELECT * FROM training_sessions ORDER BY date DESC")
    fun getAllTrainingSessions(): Flow<List<TrainingSession>>

    /**
     * Gets sessions within a date range,  showing weekly/monthly views
     */
    @Query("SELECT * FROM training_sessions WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getTrainingSessionsBetweenDates(startDate: Date, endDate: Date): Flow<List<TrainingSession>>

    /**
     * Gets a single session by ID
     */
    @Query("SELECT * FROM training_sessions WHERE id = :id")
    suspend fun getTrainingSessionById(id: Long): TrainingSession?

    /**
     * Counts how many sessions were completed in a date range
     * isCompleted = 1 means true in SQL
     *  showing "You completed 5 workouts this week"
     */
    @Query("SELECT COUNT(*) FROM training_sessions WHERE isCompleted = 1 AND date >= :startDate AND date <= :endDate")
    fun getCompletedSessionsCount(startDate: Date, endDate: Date): Flow<Int>

    /**
     * Calculates total training time for completed sessions in a date range
     * I only count completed sessions because planned sessions don't count yet
     * Returns Int? because if there are no completed sessions, SUM returns null
     */
    @Query("SELECT SUM(duration) FROM training_sessions WHERE isCompleted = 1 AND date >= :startDate AND date <= :endDate")
    fun getTotalTrainingTimeBetweenDates(startDate: Date, endDate: Date): Flow<Int?>

    /**
     * adds a new training session
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingSession(session: TrainingSession): Long

    /**
     * Updates an existing training session
     */
    @Update
    suspend fun updateTrainingSession(session: TrainingSession)

    /**
     * Deletes a training session
     */
    @Delete
    suspend fun deleteTrainingSession(session: TrainingSession)

    /**
     * Deletes a session by ID
     */
    @Query("DELETE FROM training_sessions WHERE id = :id")
    suspend fun deleteTrainingSessionById(id: Long)

    /**
     * Updates just the completion status
     */
    @Query("UPDATE training_sessions SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletionStatus(id: Long, isCompleted: Boolean)
}
