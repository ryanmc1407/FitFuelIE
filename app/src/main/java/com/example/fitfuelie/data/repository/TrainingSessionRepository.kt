package com.example.fitfuelie.data.repository

import com.example.fitfuelie.data.local.dao.TrainingSessionDao
import com.example.fitfuelie.data.local.entity.TrainingSession
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * TrainingSessionRepository
 * 
 * Handles all operations related to training sessions (workouts).
 * This repository manages workout plans and tracks completed sessions.
 */
class TrainingSessionRepository(
    private val trainingSessionDao: TrainingSessionDao
) {

    /**
     * Gets all training sessions
     * Returns Flow so the calendar updates automatically when sessions are added/removed
     */
    fun getAllTrainingSessions(): Flow<List<TrainingSession>> =
        trainingSessionDao.getAllTrainingSessions()

    /**
     * Gets sessions within a date range
     * Perfect for showing weekly/monthly calendar views
     */
    fun getTrainingSessionsBetweenDates(startDate: Date, endDate: Date): Flow<List<TrainingSession>> =
        trainingSessionDao.getTrainingSessionsBetweenDates(startDate, endDate)


    suspend fun getTrainingSessionById(id: Long): TrainingSession? =
        trainingSessionDao.getTrainingSessionById(id)

    /**
     * Counts completed sessions in a date range
     *
     */
    fun getCompletedSessionsCount(startDate: Date, endDate: Date): Flow<Int> =
        trainingSessionDao.getCompletedSessionsCount(startDate, endDate)

    /**
     * Calculates total training time for completed sessions
     */
    fun getTotalTrainingTimeBetweenDates(startDate: Date, endDate: Date): Flow<Int?> =
        trainingSessionDao.getTotalTrainingTimeBetweenDates(startDate, endDate)

    /**
     * Adds a new training session to the calendar
     */
    suspend fun insertTrainingSession(session: TrainingSession): Long =
        trainingSessionDao.insertTrainingSession(session)

    /**
     * Updates an existing training session
     */
    suspend fun updateTrainingSession(session: TrainingSession) =
        trainingSessionDao.updateTrainingSession(session)

    /**
     * Deletes a training session
     */
    suspend fun deleteTrainingSession(session: TrainingSession) =
        trainingSessionDao.deleteTrainingSession(session)

    /**
     * Deletes a session by ID
     */
    suspend fun deleteTrainingSessionById(id: Long) =
        trainingSessionDao.deleteTrainingSessionById(id)

    /**
     * Updates the completion status of a session
     */
    suspend fun updateCompletionStatus(id: Long, isCompleted: Boolean) =
        trainingSessionDao.updateCompletionStatus(id, isCompleted)
}
