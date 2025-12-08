package com.example.fitfuelie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfuelie.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


/**
 * MainViewModel
 * 
 * Controls whether to show onboarding or the main app.
 * Checks if user has completed onboarding by looking at their profile.
 */
class MainViewModel(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    // I want to know if I should show the onboarding screen.
    // I get the user profile from the repository, check if onboarding is completed, and convert it to a StateFlow.
    // 'stateIn' converts a cold Flow to a hot StateFlow, so the UI can always get the latest value.
    val shouldShowOnboarding = userProfileRepository.getUserProfile()
        .map { profile -> profile?.isOnboardingCompleted != true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val userName = userProfileRepository.getUserProfile()
        .map { profile -> profile?.name ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    // This function is called when the user finishes the onboarding.
    // I launch a coroutine in the viewModelScope because writing to the database takes time.
    fun markOnboardingCompleted() {
        viewModelScope.launch {
            userProfileRepository.updateOnboardingStatus(true)
        }
    }
}
