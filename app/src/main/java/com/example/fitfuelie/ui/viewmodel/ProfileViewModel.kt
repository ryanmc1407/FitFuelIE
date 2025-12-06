package com.example.fitfuelie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfuelie.data.local.entity.UserProfile
import com.example.fitfuelie.data.model.DietaryPreference
import com.example.fitfuelie.data.model.Goal
import com.example.fitfuelie.data.model.TrainingFrequency
import com.example.fitfuelie.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class ProfileViewModel(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    val userProfile = userProfileRepository.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _editedProfile = MutableStateFlow<UserProfile?>(null)
    val editedProfile: StateFlow<UserProfile?> = _editedProfile.asStateFlow()

    fun startEditing() {
        _isEditing.value = true
        _editedProfile.value = userProfile.value
    }

    fun cancelEditing() {
        _isEditing.value = false
        _editedProfile.value = null
    }

    fun updateName(name: String) {
        _editedProfile.value = _editedProfile.value?.copy(name = name)
    }

    fun updateGoal(goal: Goal) {
        _editedProfile.value = _editedProfile.value?.copy(goal = goal)
    }

    fun updateTrainingFrequency(frequency: TrainingFrequency) {
        _editedProfile.value = _editedProfile.value?.copy(trainingFrequency = frequency)
    }

    fun updateDietaryPreference(preference: DietaryPreference) {
        _editedProfile.value = _editedProfile.value?.copy(dietaryPreference = preference)
    }

    fun updateNutritionTargets(
        calories: Int,
        protein: Float,
        carbs: Float,
        fat: Float
    ) {
        _editedProfile.value = _editedProfile.value?.copy(
            dailyCalorieTarget = calories,
            dailyProteinTarget = protein,
            dailyCarbTarget = carbs,
            dailyFatTarget = fat
        )
    }

    fun saveProfile() {
        val profile = _editedProfile.value ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                userProfileRepository.updateUserProfile(profile)
                _isEditing.value = false
                _editedProfile.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userProfileRepository.updateOnboardingStatus(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun validateProfile(profile: UserProfile?): Boolean {
        return profile != null &&
               profile.name.isNotBlank() &&
               profile.dailyCalorieTarget > 0 &&
               profile.dailyProteinTarget > 0 &&
               profile.dailyCarbTarget > 0 &&
               profile.dailyFatTarget > 0
    }
}
