package com.example.fitfuelie.data.local.dao

import androidx.room.*
import com.example.fitfuelie.data.local.entity.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * UserProfileDao
 * 
 * Handles all database operations for the user profile.
 * Since there's only one user, I always query for id = 1.
 * 
 * I made this separate from other DAOs because the user profile is special,
 * it's created during onboarding and updated throughout the app lifecycle.
 */
@Dao
interface UserProfileDao {

    /**
     * Gets the user profile (always id = 1)
     * Returns Flow so the UI can react to profile changes
     * Returns UserProfile? because the profile might not exist yet (before onboarding)
     */
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    /**
     * Gets the user profile synchronously (not reactive)
     * Useful when I need the profile immediately, like during app startup
     * suspend means it won't block the UI thread
     */
    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileSync(): UserProfile?

    /**
     * Inserts a new user profile
     * REPLACE means if one already exists, it gets replaced
     * This is useful for the initial profile creation
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile): Long

    /**
     * Updates the existing user profile
     * Room matches by primary key and updates all fields
     */
    @Update
    suspend fun updateUserProfile(profile: UserProfile)

    /**
     * Updates just the onboarding status
     * I use a custom query here because I only want to update one field
     * This is more efficient than loading the whole profile, changing one field, and saving it
     */
    @Query("UPDATE user_profile SET isOnboardingCompleted = :completed WHERE id = 1")
    suspend fun updateOnboardingStatus(completed: Boolean)

    /**
     * Deletes the user profile
     * Probably won't use this much, but it's good to have for testing or account deletion
     */
    @Query("DELETE FROM user_profile WHERE id = 1")
    suspend fun deleteUserProfile()
}
