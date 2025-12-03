package com.example.fitfuelie.data.local.dao

import androidx.room.*
import com.example.fitfuelie.data.local.entity.TrainingSession
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TrainingSessionDao {

    @Query("SELECT * FROM training_sessions ORDER BY date DESC")
    fun getAllTrainingSessions(): Flow<List<TrainingSession>>

    @Query("SELECT * FROM training_sessions WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getTrainingSessionsBetweenDates(startDate: Date, endDate: Date): Flow<List<TrainingSession>>

    @Query("SELECT * FROM training_sessions WHERE id = :id")
    suspend fun getTrainingSessionById(id: Long): TrainingSession?

    @Query("SELECT COUNT(*) FROM training_sessions WHERE isCompleted = 1 AND date >= :startDate AND date <= :endDate")
    fun getCompletedSessionsCount(startDate: Date, endDate: Date): Flow<Int>

    @Query("SELECT SUM(duration) FROM training_sessions WHERE isCompleted = 1 AND date >= :startDate AND date <= :endDate")
    fun getTotalTrainingTimeBetweenDates(startDate: Date, endDate: Date): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrainingSession(session: TrainingSession): Long

    @Update
    suspend fun updateTrainingSession(session: TrainingSession)

    @Delete
    suspend fun deleteTrainingSession(session: TrainingSession)

    @Query("DELETE FROM training_sessions WHERE id = :id")
    suspend fun deleteTrainingSessionById(id: Long)

    @Query("UPDATE training_sessions SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletionStatus(id: Long, isCompleted: Boolean)
}
