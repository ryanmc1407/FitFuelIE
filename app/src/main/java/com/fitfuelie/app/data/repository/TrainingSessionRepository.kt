package com.fitfuelie.app.data.repository

import com.fitfuelie.app.data.dao.TrainingSessionDao
import com.fitfuelie.app.data.model.TrainingSession
import com.fitfuelie.app.data.model.TrainingType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingSessionRepository @Inject constructor(
    private val trainingSessionDao: TrainingSessionDao
) {
    // Create
    suspend fun insertTrainingSession(session: TrainingSession): Long =
        trainingSessionDao.insertTrainingSession(session)

    // Read
    fun getAllTrainingSessions(): Flow<List<TrainingSession>> =
        trainingSessionDao.getAllTrainingSessions()

    fun getTrainingSessionsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TrainingSession>> =
        trainingSessionDao.getTrainingSessionsForDateRange(startDate.atStartOfDay(), endDate.atTime(23, 59, 59))

    fun getTrainingSessionsByType(trainingType: TrainingType): Flow<List<TrainingSession>> =
        trainingSessionDao.getTrainingSessionsByType(trainingType)

    fun getCompletedSessionsCountForDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<Int> =
        trainingSessionDao.getCompletedSessionsCountForDateRange(startDate.toLocalDate(), endDate.toLocalDate())

    fun getTotalTrainingTimeForDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<Int?> =
        trainingSessionDao.getTotalTrainingTimeForDateRange(startDate.toLocalDate(), endDate.toLocalDate())

    // Update
    suspend fun updateTrainingSession(session: TrainingSession) =
        trainingSessionDao.updateTrainingSession(session)

    // Delete
    suspend fun deleteTrainingSession(session: TrainingSession) =
        trainingSessionDao.deleteTrainingSession(session)

    suspend fun getTrainingSessionById(sessionId: Long): TrainingSession? =
        trainingSessionDao.getTrainingSessionById(sessionId)
}
