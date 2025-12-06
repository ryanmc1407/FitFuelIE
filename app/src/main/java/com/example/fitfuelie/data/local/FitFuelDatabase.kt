package com.example.fitfuelie.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fitfuelie.data.local.converter.TypeConverters as FitFuelTypeConverters
import com.example.fitfuelie.data.local.dao.GroceryItemDao
import com.example.fitfuelie.data.local.dao.MealDao
import com.example.fitfuelie.data.local.dao.TrainingSessionDao
import com.example.fitfuelie.data.local.dao.UserProfileDao
import com.example.fitfuelie.data.local.entity.GroceryItem
import com.example.fitfuelie.data.local.entity.Meal
import com.example.fitfuelie.data.local.entity.TrainingSession
import com.example.fitfuelie.data.local.entity.UserProfile

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
@TypeConverters(FitFuelTypeConverters::class)
abstract class FitFuelDatabase : RoomDatabase() {

    abstract fun mealDao(): MealDao
    abstract fun trainingSessionDao(): TrainingSessionDao
    abstract fun groceryItemDao(): GroceryItemDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        const val DATABASE_NAME = "fitfuel_database"
    }
}
