package com.example.fitfuelie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfuelie.data.repository.MealRepository
import com.example.fitfuelie.data.repository.TrainingSessionRepository
import com.example.fitfuelie.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * DashboardViewModel
 * 
 * Manages data for the dashboard screen, shows today's nutrition and training stats.
 * Combines data from multiple repositories to create a summary view.
 */
class DashboardViewModel(
    private val mealRepository: MealRepository,
    private val trainingRepository: TrainingSessionRepository,
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    private val tomorrow = Calendar.getInstance().apply {
        time = today
        add(Calendar.DAY_OF_MONTH, 1)
    }.time

    val userProfile = userProfileRepository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todaysNutrition = combine(
        mealRepository.getTotalCaloriesBetweenDates(today, tomorrow),
        mealRepository.getTotalProteinBetweenDates(today, tomorrow),
        mealRepository.getTotalCarbsBetweenDates(today, tomorrow),
        mealRepository.getTotalFatBetweenDates(today, tomorrow)
    ) { calories, protein, carbs, fat ->
        NutritionSummary(
            calories = calories ?: 0,
            protein = protein ?: 0f,
            carbs = carbs ?: 0f,
            fat = fat ?: 0f
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NutritionSummary(0, 0f, 0f, 0f))

    val todaysTrainingStats = combine(
        trainingRepository.getCompletedSessionsCount(today, tomorrow),
        trainingRepository.getTotalTrainingTimeBetweenDates(today, tomorrow)
    ) { completedCount, totalTime ->
        TrainingStats(
            completedSessions = completedCount,
            totalTrainingTime = totalTime ?: 0
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TrainingStats(0, 0))

    val todaysMeals = mealRepository.getMealsBetweenDates(today, tomorrow)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todaysSessions = trainingRepository.getTrainingSessionsBetweenDates(today, tomorrow)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleSessionCompletion(sessionId: Long, completed: Boolean) {
        viewModelScope.launch {
            trainingRepository.updateCompletionStatus(sessionId, completed)
        }
    }
}

data class NutritionSummary(
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float
)

data class TrainingStats(
    val completedSessions: Int,
    val totalTrainingTime: Int // in minutes
)
