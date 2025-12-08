package com.example.fitfuelie.data.model

/**
 * TrainingType Enum
 * 
 * Represents different types of workouts/exercises.
 * Used when creating training sessions to categorize workouts.
 * This helps track what kind of training the user does and calculate appropriate recovery times.
 */
enum class TrainingType {
    STRENGTH,            // Weight lifting, resistance training
    CARDIO,              // Running, cycling, swimming
    FLEXIBILITY,         //  stretching
    SPORTS_SPECIFIC_GAA, // GAA specific training
    RECOVERY,            // Light activity for recovery days
    HIIT,                // High-intensity interval training
    OTHER                // Any other type of workout
}
