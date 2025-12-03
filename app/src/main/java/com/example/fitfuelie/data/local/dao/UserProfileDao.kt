package com.example.fitfuelie.data.local.dao

import androidx.room.*
import com.example.fitfuelie.data.local.entity.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile): Long

    @Update
    suspend fun updateUserProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET isOnboardingCompleted = :completed WHERE id = 1")
    suspend fun updateOnboardingStatus(completed: Boolean)

    @Query("DELETE FROM user_profile WHERE id = 1")
    suspend fun deleteUserProfile()
}
