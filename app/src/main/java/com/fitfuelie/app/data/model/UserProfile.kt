package com.fitfuelie.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.fitfuelie.app.data.Converters

@Entity(tableName = "user_profile")
@TypeConverters(Converters::class)
data class UserProfile(
    @PrimaryKey
    val id: Long = 1, // Only one profile per user
    val name: String? = null,
    val goal: FitnessGoal? = null,
    val trainingFrequency: TrainingFrequency? = null,
    val dietaryPreferences: Set<DietaryPreference> = emptySet(),
    val dailyCalorieGoal: Int? = null,
    val dailyProteinGoal: Double? = null,
    val dailyCarbGoal: Double? = null,
    val dailyFatGoal: Double? = null,
    val onboardingCompleted: Boolean = false
)

enum class FitnessGoal {
    BUILD_MUSCLE,
    LOSE_WEIGHT,
    IMPROVE_PERFORMANCE,
    MAINTAIN_FITNESS
}

enum class TrainingFrequency {
    DAYS_2_3,
    DAYS_4_5,
    DAYS_6_PLUS
}

enum class DietaryPreference {
    VEGETARIAN,
    VEGAN,
    GLUTEN_FREE,
    KETO,
    NO_RESTRICTIONS
}
