package com.example.fitfuelie.data.local.converter

import androidx.room.TypeConverter
import com.example.fitfuelie.data.model.*
import java.util.Date

class TypeConverters {

    // Date converters
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // Goal enum converters
    @TypeConverter
    fun fromGoal(goal: Goal): String {
        return goal.name
    }

    @TypeConverter
    fun toGoal(value: String): Goal {
        return Goal.valueOf(value)
    }

    // TrainingFrequency enum converters
    @TypeConverter
    fun fromTrainingFrequency(frequency: TrainingFrequency): String {
        return frequency.name
    }

    @TypeConverter
    fun toTrainingFrequency(value: String): TrainingFrequency {
        return TrainingFrequency.valueOf(value)
    }

    // DietaryPreference enum converters
    @TypeConverter
    fun fromDietaryPreference(preference: DietaryPreference): String {
        return preference.name
    }

    @TypeConverter
    fun toDietaryPreference(value: String): DietaryPreference {
        return DietaryPreference.valueOf(value)
    }

    // MealType enum converters
    @TypeConverter
    fun fromMealType(type: MealType): String {
        return type.name
    }

    @TypeConverter
    fun toMealType(value: String): MealType {
        return MealType.valueOf(value)
    }

    // TrainingType enum converters
    @TypeConverter
    fun fromTrainingType(type: TrainingType): String {
        return type.name
    }

    @TypeConverter
    fun toTrainingType(value: String): TrainingType {
        return TrainingType.valueOf(value)
    }

    // Intensity enum converters
    @TypeConverter
    fun fromIntensity(intensity: Intensity): String {
        return intensity.name
    }

    @TypeConverter
    fun toIntensity(value: String): Intensity {
        return Intensity.valueOf(value)
    }

    // GroceryCategory enum converters
    @TypeConverter
    fun fromGroceryCategory(category: GroceryCategory): String {
        return category.name
    }

    @TypeConverter
    fun toGroceryCategory(value: String): GroceryCategory {
        return GroceryCategory.valueOf(value)
    }
}
