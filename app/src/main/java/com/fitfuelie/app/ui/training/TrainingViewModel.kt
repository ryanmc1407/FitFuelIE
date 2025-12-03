package com.fitfuelie.app.ui.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitfuelie.app.data.DatabaseProvider
import com.fitfuelie.app.data.model.TrainingSession
import com.fitfuelie.app.data.repository.TrainingSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

class TrainingViewModel(
    private val trainingSessionRepository: TrainingSessionRepository
) : ViewModel() {

    // UI state
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    // Training sessions for selected date
    val trainingSessionsForSelectedDate: Flow<List<TrainingSession>> = combine(
        trainingSessionRepository.getAllTrainingSessions(),
        _selectedDate
    ) { sessions, date ->
        sessions.filter { session ->
            session.dateTime.toLocalDate() == date
        }
    }

    // Completed sessions count for selected date
    val completedSessionsCount = combine(
        trainingSessionRepository.getCompletedSessionsCountForDateRange(
            _selectedDate.value.atStartOfDay(),
            _selectedDate.value.atTime(23, 59, 59)
        ),
        trainingSessionsForSelectedDate
    ) { completedCount, allSessions ->
        Pair(completedCount, allSessions.size)
    }

    // Total training time for selected date
    val totalTrainingTime = trainingSessionRepository.getTotalTrainingTimeForDateRange(
        _selectedDate.value.atStartOfDay(),
        _selectedDate.value.atTime(23, 59, 59)
    )

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addTrainingSession(session: TrainingSession) {
        viewModelScope.launch {
            trainingSessionRepository.insertTrainingSession(session)
        }
    }

    fun updateTrainingSession(session: TrainingSession) {
        viewModelScope.launch {
            trainingSessionRepository.updateTrainingSession(session)
        }
    }

    fun deleteTrainingSession(session: TrainingSession) {
        viewModelScope.launch {
            trainingSessionRepository.deleteTrainingSession(session)
        }
    }

    fun toggleSessionCompletion(session: TrainingSession) {
        viewModelScope.launch {
            val updatedSession = session.copy(completed = !session.completed)
            trainingSessionRepository.updateTrainingSession(updatedSession)
        }
    }

    suspend fun getTrainingSessionById(sessionId: Long): TrainingSession? {
        return trainingSessionRepository.getTrainingSessionById(sessionId)
    }
}
