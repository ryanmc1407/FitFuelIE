package com.example.fitfuelie.data.model

/**
 * Goal Enum
 * 
 * Represents the user's fitness goal.
 * This is set during onboarding and used to calculate calorie targets.
 * 
 * I use an enum to ensure only valid goals can be selected.
 * The app can use this to personalize recommendations and targets.
 */
enum class Goal {
    BUILD_MUSCLE,        // Goal: Gain muscle mass
    LOSE_WEIGHT,         // Goal: Reduce body weight
    IMPROVE_PERFORMANCE, // Goal: Get better at sports/activities
    MAINTAIN_FITNESS     // Goal: Keep current fitness level
}
