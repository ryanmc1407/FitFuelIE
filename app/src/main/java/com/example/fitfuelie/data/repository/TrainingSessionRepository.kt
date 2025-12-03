package com.example.fitfuelie.data.repository

import com.example.fitfuelie.data.local.dao.TrainingSessionDao
import com.example.fitfuelie.data.local.entity.TrainingSession
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingSessionRepository @Inject constructor(
    private val trainingSessionDao: TrainingSessionDao
) {

    fun getAllTrainingSessions(): Flow<List<TrainingSession>> =
        trainingSessionDao.getAllTrainingSessions()

    fun getTrainingSessionsBetweenDates(startDate: Date, endDate: Date): Flow<List<TrainingSession>> =
        trainingSessionDao.getTrainingSessionsBetweenDates(startDate, endDate)

    suspend fun getTrainingSessionById(id: Long): TrainingSession? =
        trainingSessionDao.getTrainingSessionById(id)

    fun getCompletedSessionsCount(startDate: Date, endDate: Date): Flow<Int> =
        trainingSessionDao.getCompletedSessionsCount(startDate, endDate)

    fun getTotalTrainingTimeBetweenDates(startDate: Date, endDate: Date): Flow<Int?> =
        trainingSessionDao.getTotalTrainingTimeBetweenDates(startDate, endDate)

    suspend fun insertTrainingSession(session: TrainingSession): Long =
        trainingSessionDao.insertTrainingSession(session)

    suspend fun updateTrainingSession(session: TrainingSession) =
        trainingSessionDao.updateTrainingSession(session)

    suspend fun deleteTrainingSession(session: TrainingSession) =
        trainingSessionDao.deleteTrainingSession(session)

    suspend fun deleteTrainingSessionById(id: Long) =
        trainingSessionDao.deleteTrainingSessionById(id)

    suspend fun updateCompletionStatus(id: Long, isCompleted: Boolean) =
        trainingSessionDao.updateCompletionStatus(id, isCompleted)
}
