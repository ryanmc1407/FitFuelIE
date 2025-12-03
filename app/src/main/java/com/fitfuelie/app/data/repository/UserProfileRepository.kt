package com.fitfuelie.app.data.repository

import com.fitfuelie.app.data.dao.UserProfileDao
import com.fitfuelie.app.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao
) {
    fun getUserProfile(): Flow<UserProfile?> = userProfileDao.getUserProfile()

    suspend fun getUserProfileSync(): UserProfile? = userProfileDao.getUserProfileSync()

    suspend fun saveUserProfile(profile: UserProfile) = userProfileDao.insertUserProfile(profile)

    suspend fun updateUserProfile(profile: UserProfile) = userProfileDao.updateUserProfile(profile)

    suspend fun setOnboardingCompleted(completed: Boolean) =
        userProfileDao.setOnboardingCompleted(completed)

    suspend fun isOnboardingCompleted(): Boolean {
        val profile = userProfileDao.getUserProfileSync()
        return profile?.onboardingCompleted == true
    }
}
