package com.example.fitfuelie.data.repository

import com.example.fitfuelie.data.local.dao.UserProfileDao
import com.example.fitfuelie.data.local.entity.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * UserProfileRepository
 * 
 * Handles all operations related to the user profile.
 * The profile is created during onboarding and updated throughout the app.
 * 
 * Since there's only one user profile, all operations work with id = 1.
 */
class UserProfileRepository(
    private val userProfileDao: UserProfileDao
) {

    /**
     * Gets the user profile
     * Returns Flow so the UI updates when the profile changes
     * Returns UserProfile? because the profile might not exist yet.
     */
    fun getUserProfile(): Flow<UserProfile?> = userProfileDao.getUserProfile()

    /**
     * Gets the user profile synchronously
     */
    suspend fun getUserProfileSync(): UserProfile? = userProfileDao.getUserProfileSync()

    /**
     * Creates a new user profile
     */
    suspend fun insertUserProfile(profile: UserProfile): Long = userProfileDao.insertUserProfile(profile)

    /**
     * Updates the user profile
     * Used when the user changes their goals, weight and diet.
     */
    suspend fun updateUserProfile(profile: UserProfile) = userProfileDao.updateUserProfile(profile)

    /**
     * Updates just the onboarding status
     * Called when the user finishes the onboarding process
     */
    suspend fun updateOnboardingStatus(completed: Boolean) =
        userProfileDao.updateOnboardingStatus(completed)

    /**
     * Deletes the user profile
     * Probably won't use this much, but good to have for testing
     */
    suspend fun deleteUserProfile() = userProfileDao.deleteUserProfile()
}
