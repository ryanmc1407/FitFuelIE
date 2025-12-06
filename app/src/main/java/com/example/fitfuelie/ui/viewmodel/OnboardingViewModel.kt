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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


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

    private val _weight = MutableStateFlow("")
    val weight: StateFlow<String> = _weight.asStateFlow()

    fun updateWeight(weightInput: String) {
        // Allow only numbers and one decimal point
        if (weightInput.isEmpty() || weightInput.matches(Regex("^\\d*\\.?\\d*$"))) {
            _weight.value = weightInput
        }
    }

    fun nextStep() {
        if (_currentStep.value < 3) { // Increased steps from 2 to 3
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
        val weightVal = _weight.value.toFloatOrNull() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Calculate default nutrition targets based on selections
                val (calories, protein, carbs, fat) = calculateDefaultTargets(goal, trainingFrequency, weightVal)

                val userProfile = UserProfile(
                    name = name,
                    goal = goal,
                    trainingFrequency = trainingFrequency,
                    dietaryPreference = dietaryPreference,
                    weight = weightVal,
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

    private fun calculateDefaultTargets(
        goal: Goal, 
        trainingFrequency: TrainingFrequency,
        weightKg: Float
    ): Quadruple<Int, Float, Float, Float> {
        // 1. Estimate BMR (Basal Metabolic Rate) using simplified formula
        // BMR ≈ 22 * weight_in_kg (Rough estimate for average person)
        val bmr = 22 * weightKg

        // 2. Estimate TDEE (Total Daily Energy Expenditure) based on activity
        val activityMultiplier = when (trainingFrequency) {
            TrainingFrequency.TWO_THREE_DAYS -> 1.375f // Lightly active
            TrainingFrequency.FOUR_FIVE_DAYS -> 1.55f  // Moderately active
            TrainingFrequency.SIX_PLUS_DAYS -> 1.725f  // Very active
        }
        
        var targetCalories = (bmr * activityMultiplier).toInt()

        // 3. Adjust for Goal
        when (goal) {
            Goal.LOSE_WEIGHT -> targetCalories -= 500
            Goal.BUILD_MUSCLE -> targetCalories += 300
            Goal.IMPROVE_PERFORMANCE -> targetCalories += 0 // Maintenance/Performance
            Goal.MAINTAIN_FITNESS -> targetCalories += 0
        }

        // Ensure calories don't go too low
        if (targetCalories < 1200) targetCalories = 1200

        // 4. Calculate Macros
        // Protein: 2g per kg of bodyweight (good for active individuals)
        val proteinGrams = (2.0f * weightKg).coerceAtLeast(50f) // Minimum 50g
        
        // Fat: 0.8g per kg of bodyweight
        val fatGrams = (0.8f * weightKg).coerceAtLeast(30f) // Minimum 30g

        // Carbs: Remaining calories
        // Protein = 4 cal/g, Fat = 9 cal/g, Carbs = 4 cal/g
        val proteinCals = proteinGrams * 4
        val fatCals = fatGrams * 9
        val remainingCals = targetCalories - proteinCals - fatCals
        val carbGrams = (remainingCals / 4).coerceAtLeast(50f) // Minimum 50g

        return Quadruple(targetCalories, proteinGrams, carbGrams, fatGrams)
    }

    val isNextEnabled: StateFlow<Boolean> = kotlinx.coroutines.flow.combine(
        _currentStep,
        _selectedGoal,
        _selectedTrainingFrequency,
        _selectedDietaryPreference,
        _userName,
        _weight
    ) { step, goal, freq, diet, name, weight ->
        when (step) {
            0 -> goal != null
            1 -> freq != null
            2 -> weight.toFloatOrNull() != null && weight.toFloat() > 0
            3 -> diet != null && name.isNotBlank()
            else -> false
        }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), false)

    fun clearError() {
        _error.value = null
    }
}

// Helper data class for returning multiple values
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
