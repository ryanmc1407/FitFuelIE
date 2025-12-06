package com.example.fitfuelie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfuelie.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class MainViewModel(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    val shouldShowOnboarding = userProfileRepository.getUserProfile()
        .map { profile -> profile?.isOnboardingCompleted != true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val userName = userProfileRepository.getUserProfile()
        .map { profile -> profile?.name ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    fun markOnboardingCompleted() {
        viewModelScope.launch {
            userProfileRepository.updateOnboardingStatus(true)
        }
    }
}
