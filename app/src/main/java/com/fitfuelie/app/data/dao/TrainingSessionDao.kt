package com.fitfuelie.app.data.dao

import androidx.room.*
import com.fitfuelie.app.data.model.TrainingSession
import com.fitfuelie.app.data.model.TrainingType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TrainingSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingSession(session: TrainingSession): Long

    @Update
    suspend fun updateTrainingSession(session: TrainingSession)

    @Delete
    suspend fun deleteTrainingSession(session: TrainingSession)

    @Query("SELECT * FROM training_sessions WHERE id = :sessionId")
    suspend fun getTrainingSessionById(sessionId: Long): TrainingSession?

    @Query("SELECT * FROM training_sessions ORDER BY dateTime DESC")
    fun getAllTrainingSessions(): Flow<List<TrainingSession>>

    @Query("SELECT * FROM training_sessions WHERE completed = :completed ORDER BY dateTime DESC")
    fun getTrainingSessionsByCompletion(completed: Boolean): Flow<List<TrainingSession>>

    @Query("SELECT * FROM training_sessions WHERE dateTime >= :startDate AND dateTime < :endDate ORDER BY dateTime")
    fun getTrainingSessionsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<TrainingSession>>

    @Query("SELECT * FROM training_sessions WHERE trainingType = :trainingType ORDER BY dateTime DESC")
    fun getTrainingSessionsByType(trainingType: TrainingType): Flow<List<TrainingSession>>

    @Query("SELECT COUNT(*) FROM training_sessions WHERE dateTime >= :startDate AND dateTime < :endDate AND completed = 1")
    fun getCompletedSessionsCountForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Int>

    @Query("SELECT SUM(duration) FROM training_sessions WHERE dateTime >= :startDate AND dateTime < :endDate AND completed = 1")
    fun getTotalTrainingTimeForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Int?>
}
