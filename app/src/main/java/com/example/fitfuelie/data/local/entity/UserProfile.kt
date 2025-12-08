package com.example.fitfuelie.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fitfuelie.data.model.DietaryPreference
import com.example.fitfuelie.data.model.Goal
import com.example.fitfuelie.data.model.TrainingFrequency

/**
 * UserProfile Entity
 * 
 * This stores all the information about the user - their goals, preferences, and targets.
 * Since there's only one user per app, I always use ID = 1. This makes it simple!
 * 
 * I use this to personalize the app, like showing the right calorie goals
 * and suggesting meals that match their dietary preferences.
 */
@Entity(tableName = "user_profile")
data class UserProfile(
    // I always use ID = 1 because there's only one user profile
    // No autoGenerate needed since I know it's always 1
    @PrimaryKey
    val id: Long = 1,
    
    // User's name
    val name: String,
    
    // Their fitness goal , lose weight, gain muscle, maintain, etc.
    // This helps me calculate the right calorie targets
    val goal: Goal,
    
    // How often they work out
    val trainingFrequency: TrainingFrequency,
    
    // Dietary restrictions, vegetarian, vegan, etc.
    // I use this to filter meal suggestions
    val dietaryPreference: DietaryPreference,
    
    // Current weight in kilograms (kg)
    // I use Float because weight can have decimals like 70.5 kg
    val weight: Float,
    
    // Daily nutrition targets, what the user wants to hit each day
    // These are calculated based on their goal and weight
    val dailyCalorieTarget: Int,
    val dailyProteinTarget: Float,
    val dailyCarbTarget: Float,
    val dailyFatTarget: Float,
    
    // Tracks if the user has finished the onboarding process
    // If false, I show them the setup screen when they open the app
    val isOnboardingCompleted: Boolean = false
)
