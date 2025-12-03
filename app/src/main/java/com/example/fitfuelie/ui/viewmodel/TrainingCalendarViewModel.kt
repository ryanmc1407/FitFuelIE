package com.example.fitfuelie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfuelie.data.local.entity.TrainingSession
import com.example.fitfuelie.data.model.Intensity
import com.example.fitfuelie.data.model.TrainingType
import com.example.fitfuelie.data.repository.TrainingSessionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject



class TrainingCalendarViewModel(
    private val trainingRepository: TrainingSessionRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(Calendar.getInstance().time)
    val selectedDate: StateFlow<Date> = _selectedDate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val sessionsForSelectedDate = combine(
        _selectedDate,
        trainingRepository.getAllTrainingSessions()
    ) { selectedDate, allSessions ->
        val startOfDay = Calendar.getInstance().apply {
            time = selectedDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val endOfDay = Calendar.getInstance().apply {
            time = startOfDay
            add(Calendar.DAY_OF_MONTH, 1)
        }.time

        allSessions.filter { session ->
            session.date >= startOfDay && session.date < endOfDay
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSessions = trainingRepository.getAllTrainingSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDate(date: Date) {
        _selectedDate.value = date
    }

    fun addTrainingSession(
        title: String,
        type: TrainingType,
        intensity: Intensity,
        duration: Int,
        notes: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val session = TrainingSession(
                    title = title,
                    type = type,
                    intensity = intensity,
                    duration = duration,
                    date = _selectedDate.value,
                    notes = notes
                )
                trainingRepository.insertTrainingSession(session)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateTrainingSession(session: TrainingSession) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                trainingRepository.updateTrainingSession(session)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteTrainingSession(session: TrainingSession) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                trainingRepository.deleteTrainingSession(session)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleSessionCompletion(sessionId: Long, completed: Boolean) {
        viewModelScope.launch {
            trainingRepository.updateCompletionStatus(sessionId, completed)
        }
    }

    fun getSessionById(id: Long): Flow<TrainingSession?> {
        return flow {
            val session = trainingRepository.getTrainingSessionById(id)
            emit(session)
        }
    }
}
