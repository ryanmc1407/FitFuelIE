package com.example.fitfuelie.data.repository

import com.example.fitfuelie.data.local.dao.UserProfileDao
import com.example.fitfuelie.data.local.entity.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao
) {

    fun getUserProfile(): Flow<UserProfile?> = userProfileDao.getUserProfile()

    suspend fun getUserProfileSync(): UserProfile? = userProfileDao.getUserProfileSync()

    suspend fun insertUserProfile(profile: UserProfile): Long = userProfileDao.insertUserProfile(profile)

    suspend fun updateUserProfile(profile: UserProfile) = userProfileDao.updateUserProfile(profile)

    suspend fun updateOnboardingStatus(completed: Boolean) =
        userProfileDao.updateOnboardingStatus(completed)

    suspend fun deleteUserProfile() = userProfileDao.deleteUserProfile()
}
