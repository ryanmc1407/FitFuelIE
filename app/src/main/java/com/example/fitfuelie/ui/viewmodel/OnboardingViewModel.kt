package com.example.fitfuelie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfuelie.data.local.entity.UserProfile
import com.example.fitfuelie.data.model.DietaryPreference
import com.example.fitfuelie.data.model.Goal
import com.example.fitfuelie.data.model.TrainingFrequency
import com.example.fitfuelie.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class OnboardingViewModel(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _selectedGoal = MutableStateFlow<Goal?>(null)
    val selectedGoal: StateFlow<Goal?> = _selectedGoal.asStateFlow()

    private val _selectedTrainingFrequency = MutableStateFlow<TrainingFrequency?>(null)
    val selectedTrainingFrequency: StateFlow<TrainingFrequency?> = _selectedTrainingFrequency.asStateFlow()

    private val _selectedDietaryPreference = MutableStateFlow<DietaryPreference?>(null)
    val selectedDietaryPreference: StateFlow<DietaryPreference?> = _selectedDietaryPreference.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun updateUserName(name: String) {
        _userName.value = name
    }

    fun selectGoal(goal: Goal) {
        _selectedGoal.value = goal
    }

    fun selectTrainingFrequency(frequency: TrainingFrequency) {
        _selectedTrainingFrequency.value = frequency
    }

    fun selectDietaryPreference(preference: DietaryPreference) {
        _selectedDietaryPreference.value = preference
    }

    fun nextStep() {
        if (_currentStep.value < 2) {
            _currentStep.value += 1
        }
    }

    fun previousStep() {
        if (_currentStep.value > 0) {
            _currentStep.value -= 1
        }
    }

    fun completeOnboarding() {
        val goal = _selectedGoal.value ?: return
        val trainingFrequency = _selectedTrainingFrequency.value ?: return
        val dietaryPreference = _selectedDietaryPreference.value ?: return
        val name = _userName.value.takeIf { it.isNotBlank() } ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Calculate default nutrition targets based on selections
                val (calories, protein, carbs, fat) = calculateDefaultTargets(goal, trainingFrequency)

                val userProfile = UserProfile(
                    name = name,
                    goal = goal,
                    trainingFrequency = trainingFrequency,
                    dietaryPreference = dietaryPreference,
                    dailyCalorieTarget = calories,
                    dailyProteinTarget = protein,
                    dailyCarbTarget = carbs,
                    dailyFatTarget = fat,
                    isOnboardingCompleted = true
                )

                userProfileRepository.insertUserProfile(userProfile)
            } catch (e: Exception) {
                _error.value = "Failed to save profile: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun calculateDefaultTargets(goal: Goal, trainingFrequency: TrainingFrequency): Quadruple<Int, Float, Float, Float> {
        // Base values for maintenance
        var calories = 2000
        var protein = 150f
        var carbs = 250f
        var fat = 67f

        // Adjust for goal
        when (goal) {
            Goal.BUILD_MUSCLE -> {
                calories += 500
                protein += 50
            }
            Goal.LOSE_WEIGHT -> {
                calories -= 500
                protein += 25
            }
            Goal.IMPROVE_PERFORMANCE -> {
                calories += 250
                carbs += 50
            }
            Goal.MAINTAIN_FITNESS -> {
                // Keep base values
            }
        }

        // Adjust for training frequency
        when (trainingFrequency) {
            TrainingFrequency.TWO_THREE_DAYS -> {
                calories += 200
                protein += 25
            }
            TrainingFrequency.FOUR_FIVE_DAYS -> {
                calories += 400
                protein += 50
            }
            TrainingFrequency.SIX_PLUS_DAYS -> {
                calories += 600
                protein += 75
            }
        }

        return Quadruple(calories, protein, carbs, fat)
    }

    fun canProceedToNextStep(): Boolean {
        return when (_currentStep.value) {
            0 -> _selectedGoal.value != null
            1 -> _selectedTrainingFrequency.value != null
            2 -> _selectedDietaryPreference.value != null && _userName.value.isNotBlank()
            else -> false
        }
    }

    fun clearError() {
        _error.value = null
    }
}

// Helper data class for returning multiple values
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
