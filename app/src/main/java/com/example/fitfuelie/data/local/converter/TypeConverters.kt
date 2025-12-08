package com.example.fitfuelie.data.local.converter

import androidx.room.TypeConverter
import com.example.fitfuelie.data.model.*
import java.util.Date

/**
 * TypeConverters
 * 
 * Room database can only store primitive types (String, Int, Float, etc.) directly.
 * But I want to store Dates and Enums in my database!
 * 
 * TypeConverters tell Room how to convert my custom types to/from primitives.
 * Room automatically calls these functions when saving/loading data.
 * 
 * I learned this is necessary because SQLite doesn't understand Java/Kotlin types
 * like Date or enums - it only knows basic types. So I convert them!
 */
class TypeConverters {

    /**
     * Converts a Long timestamp to a Date object
     * I store dates as Long (milliseconds since 1970) because SQLite understands Long
     * When I read from the database, I convert it back to a Date
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Converts a Date object to a Long timestamp
     * When I save to the database, I convert the Date to milliseconds
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    /**
     * Converts a Goal enum to a String
     * Enums are stored as their name (like "LOSE_WEIGHT")
     */
    @TypeConverter
    fun fromGoal(goal: Goal): String {
        return goal.name
    }

    /**
     * Converts a String back to a Goal enum
     * I use valueOf() to find the enum with that name
     */
    @TypeConverter
    fun toGoal(value: String): Goal {
        return Goal.valueOf(value)
    }

    /**
     * Converts TrainingFrequency enum to String
     */
    @TypeConverter
    fun fromTrainingFrequency(frequency: TrainingFrequency): String {
        return frequency.name
    }

    /**
     * Converts String back to TrainingFrequency enum
     */
    @TypeConverter
    fun toTrainingFrequency(value: String): TrainingFrequency {
        return TrainingFrequency.valueOf(value)
    }

    /**
     * Converts DietaryPreference enum to String
     */
    @TypeConverter
    fun fromDietaryPreference(preference: DietaryPreference): String {
        return preference.name
    }

    /**
     * Converts String back to DietaryPreference enum
     */
    @TypeConverter
    fun toDietaryPreference(value: String): DietaryPreference {
        return DietaryPreference.valueOf(value)
    }

    /**
     * Converts MealType enum to String
     */
    @TypeConverter
    fun fromMealType(type: MealType): String {
        return type.name
    }

    /**
     * Converts String back to MealType enum
     */
    @TypeConverter
    fun toMealType(value: String): MealType {
        return MealType.valueOf(value)
    }

    /**
     * Converts TrainingType enum to String
     */
    @TypeConverter
    fun fromTrainingType(type: TrainingType): String {
        return type.name
    }

    /**
     * Converts String back to TrainingType enum
     */
    @TypeConverter
    fun toTrainingType(value: String): TrainingType {
        return TrainingType.valueOf(value)
    }

    /**
     * Converts Intensity enum to String
     */
    @TypeConverter
    fun fromIntensity(intensity: Intensity): String {
        return intensity.name
    }

    /**
     * Converts String back to Intensity enum
     */
    @TypeConverter
    fun toIntensity(value: String): Intensity {
        return Intensity.valueOf(value)
    }

    /**
     * Converts GroceryCategory enum to String
     */
    @TypeConverter
    fun fromGroceryCategory(category: GroceryCategory): String {
        return category.name
    }

    /**
     * Converts String back to GroceryCategory enum
     */
    @TypeConverter
    fun toGroceryCategory(value: String): GroceryCategory {
        return GroceryCategory.valueOf(value)
    }
}
