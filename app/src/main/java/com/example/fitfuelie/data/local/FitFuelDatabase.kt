package com.example.fitfuelie.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fitfuelie.data.local.converter.TypeConverters
import com.example.fitfuelie.data.local.dao.*
import com.example.fitfuelie.data.local.entity.*

@Database(
    entities = [
        Meal::class,
        TrainingSession::class,
        GroceryItem::class,
        UserProfile::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(TypeConverters::class)
abstract class FitFuelDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao
    abstract fun trainingSessionDao(): TrainingSessionDao
    abstract fun groceryItemDao(): GroceryItemDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        const val DATABASE_NAME = "fitfuel_database"
    }
}
