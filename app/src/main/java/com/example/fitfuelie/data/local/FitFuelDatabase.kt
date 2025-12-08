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

/**
 * FitFuelDatabase
 * 
 * This is the main database class for my app. Room uses this to create the actual database.
 * 
 * The @Database annotation tells Room:
 * - Which entities (tables) to create
 * - What version the database is
 * - Whether to export the schema
 * 
 * The @TypeConverters annotation tells Room to use my TypeConverters class
 * to convert Dates and Enums to/from database-friendly types.
 * 
 * Room automatically generates the implementation of this abstract class!
 * I just define the interface and Room does the rest.
 */
@Database(
    entities = [
        Meal::class,              // Table for meals
        TrainingSession::class,   // Table for workout sessions
        GroceryItem::class,       // Table for grocery list items
        UserProfile::class        // Table for user information
    ],
    version = 2,                  // Increment this when you change the database structure
    exportSchema = false          // Set to true if you want to track schema changes
)
@TypeConverters(FitFuelTypeConverters::class)
abstract class FitFuelDatabase : RoomDatabase() {

    /**
     * These abstract functions return the DAOs (Data Access Objects).
     * Room automatically implements these for me!
     * 
     * I use these DAOs to read and write data to my tables.
     * Each DAO handles one entity type.
     */
    abstract fun mealDao(): MealDao
    abstract fun trainingSessionDao(): TrainingSessionDao
    abstract fun groceryItemDao(): GroceryItemDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        /**
         * The name of the database file on the device
         * Room will create a file called "fitfuel_database" in the app's data directory
         */
        const val DATABASE_NAME = "fitfuel_database"
    }
}
