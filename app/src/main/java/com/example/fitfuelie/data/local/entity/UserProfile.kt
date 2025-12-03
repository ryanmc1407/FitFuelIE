package com.example.fitfuelie.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fitfuelie.data.model.DietaryPreference
import com.example.fitfuelie.data.model.Goal
import com.example.fitfuelie.data.model.TrainingFrequency

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Long = 1, // Single profile, so fixed ID
    val name: String,
    val goal: Goal,
    val trainingFrequency: TrainingFrequency,
    val dietaryPreference: DietaryPreference,
    val dailyCalorieTarget: Int,
    val dailyProteinTarget: Float,
    val dailyCarbTarget: Float,
    val dailyFatTarget: Float,
    val isOnboardingCompleted: Boolean = false
)
