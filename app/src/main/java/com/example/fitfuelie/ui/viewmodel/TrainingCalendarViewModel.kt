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




class TrainingCalendarViewModel(
    private val trainingRepository: TrainingSessionRepository,
    private val userProfileRepository: com.example.fitfuelie.data.repository.UserProfileRepository
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

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun clearError() {
        _error.value = null
    }

    fun clearMessage() {
        _message.value = null
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
            _error.value = null
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
            } catch (e: Exception) {
                _error.value = "Failed to save session: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun generateDailyWorkout() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userProfile = userProfileRepository.getUserProfile().firstOrNull() ?: return@launch
                val calendar = Calendar.getInstance().apply { time = _selectedDate.value }
                val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

                val (title, type, notes) = when (userProfile.goal) {
                    com.example.fitfuelie.data.model.Goal.BUILD_MUSCLE -> getMuscleBuildingWorkout(dayOfWeek)
                    com.example.fitfuelie.data.model.Goal.LOSE_WEIGHT -> getWeightLossWorkout(dayOfWeek)
                    com.example.fitfuelie.data.model.Goal.IMPROVE_PERFORMANCE -> getPerformanceWorkout(dayOfWeek)
                    com.example.fitfuelie.data.model.Goal.MAINTAIN_FITNESS -> getMaintenanceWorkout(dayOfWeek)
                }

                if (title.isNotEmpty()) {
                    addTrainingSession(
                        title = title,
                        type = type,
                        intensity = Intensity.HIGH, // Default
                        duration = 60, // Default duration
                        notes = notes
                    )
                    _message.value = "Workout generated: $title"
                } else {
                    _message.value = "No workout plan found for today."
                }
            } catch (e: Exception) {
                _error.value = "Failed to generate workout: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun getMuscleBuildingWorkout(dayOfWeek: Int): Triple<String, TrainingType, String> {
        return when (dayOfWeek) {
            Calendar.MONDAY -> Triple("Push Day (Chest/Triceps)", TrainingType.STRENGTH, "Bench Press: 3x10\nOverhead Press: 3x10\nTricep Dips: 3x12")
            Calendar.TUESDAY -> Triple("Pull Day (Back/Biceps)", TrainingType.STRENGTH, "Pull-ups: 3x8\nBarbell Rows: 3x10\nBicep Curls: 3x12")
            Calendar.WEDNESDAY -> Triple("Rest Day", TrainingType.FLEXIBILITY, "Light stretching or yoga")
            Calendar.THURSDAY -> Triple("Leg Day (Quads/Calves)", TrainingType.STRENGTH, "Squats: 3x8\nLeg Press: 3x12\nCalf Raises: 4x15")
            Calendar.FRIDAY -> Triple("Upper Body (Shoulders/Arms)", TrainingType.STRENGTH, "Lateral Raises: 3x15\nFace Pulls: 3x15\nHammer Curls: 3x12")
            Calendar.SATURDAY -> Triple("Leg Day (Hamstrings/Glutes)", TrainingType.STRENGTH, "Deadlifts: 3x5\nLunges: 3x12\nGlute Bridges: 3x15")
            else -> Triple("Active Recovery", TrainingType.CARDIO, "Light walk or swim")
        }
    }

    private fun getWeightLossWorkout(dayOfWeek: Int): Triple<String, TrainingType, String> {
        return when (dayOfWeek) {
            Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY -> Triple("HIIT Cardio", TrainingType.HIIT, "30 mins HIIT circuit\nBurpees, Mountain Climbers, Jump Squats")
            Calendar.TUESDAY, Calendar.THURSDAY -> Triple("Full Body Strength", TrainingType.STRENGTH, "Circuit training: Squats, Pushups, Rows, Planks")
            Calendar.SATURDAY -> Triple("Long Cardio", TrainingType.CARDIO, "45-60 mins steady state cardio (Run/Cycle)")
            else -> Triple("Rest Day", TrainingType.FLEXIBILITY, "Stretching and foam rolling")
        }
    }

    private fun getPerformanceWorkout(dayOfWeek: Int): Triple<String, TrainingType, String> {
        return when (dayOfWeek) {
            Calendar.MONDAY -> Triple("Speed Work", TrainingType.HIIT, "Sprints: 10x100m")
            Calendar.WEDNESDAY -> Triple("Plyometrics", TrainingType.HIIT, "Box Jumps, Broad Jumps, Depth Jumps")
            Calendar.FRIDAY -> Triple("Strength & Power", TrainingType.STRENGTH, "Power Cleans, Snatch, Box Squats")
            else -> Triple("Endurance", TrainingType.CARDIO, "Long steady run or cycle")
        }
    }

    private fun getMaintenanceWorkout(dayOfWeek: Int): Triple<String, TrainingType, String> {
        return when (dayOfWeek) {
            Calendar.MONDAY, Calendar.THURSDAY -> Triple("Full Body Workout", TrainingType.STRENGTH, "Compound movements: Squat, Bench, Deadlift")
            Calendar.TUESDAY, Calendar.FRIDAY -> Triple("Cardio", TrainingType.CARDIO, "30 mins jogging or cycling")
            else -> Triple("Active Rest", TrainingType.FLEXIBILITY, "Yoga or stretching")
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
