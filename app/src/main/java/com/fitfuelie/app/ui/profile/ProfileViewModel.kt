package com.fitfuelie.app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitfuelie.app.data.DatabaseProvider
import com.fitfuelie.app.data.model.*
import com.fitfuelie.app.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    val userProfile: Flow<UserProfile?> = userProfileRepository.getUserProfile()

    fun updateProfile(
        name: String? = null,
        goal: FitnessGoal? = null,
        trainingFrequency: TrainingFrequency? = null,
        dietaryPreferences: Set<DietaryPreference>? = null,
        dailyCalorieGoal: Int? = null,
        dailyProteinGoal: Double? = null,
        dailyCarbGoal: Double? = null,
        dailyFatGoal: Double? = null
    ) {
        viewModelScope.launch {
            val currentProfile = userProfileRepository.getUserProfileSync() ?: UserProfile()

            val updatedProfile = currentProfile.copy(
                name = name ?: currentProfile.name,
                goal = goal ?: currentProfile.goal,
                trainingFrequency = trainingFrequency ?: currentProfile.trainingFrequency,
                dietaryPreferences = dietaryPreferences ?: currentProfile.dietaryPreferences,
                dailyCalorieGoal = dailyCalorieGoal ?: currentProfile.dailyCalorieGoal,
                dailyProteinGoal = dailyProteinGoal ?: currentProfile.dailyProteinGoal,
                dailyCarbGoal = dailyCarbGoal ?: currentProfile.dailyCarbGoal,
                dailyFatGoal = dailyFatGoal ?: currentProfile.dailyFatGoal
            )

            userProfileRepository.saveUserProfile(updatedProfile)
        }
    }

    fun resetOnboarding() {
        viewModelScope.launch {
            userProfileRepository.setOnboardingCompleted(false)
        }
    }
}
