package com.fitfuelie.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fitfuelie.app.data.dao.*
import com.fitfuelie.app.data.model.*

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
@TypeConverters(Converters::class)
abstract class FitFuelDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun trainingSessionDao(): TrainingSessionDao
    abstract fun groceryItemDao(): GroceryItemDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        const val DATABASE_NAME = "fitfuel_database"
    }
}
