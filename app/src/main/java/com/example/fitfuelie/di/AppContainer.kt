package com.example.fitfuelie.di

import android.content.Context
import androidx.room.Room
import com.example.fitfuelie.data.local.FitFuelDatabase
import com.example.fitfuelie.data.repository.*

/**
 * AppContainer
 * 
 * This is my simple dependency injection container.
 * Instead of using a fancy DI library like Dagger or Hilt, I use this simple class.
 * 

 * - Instead of classes creating their own dependencies, I create them once and pass them in
 * - This makes testing easier because I can swap in fake objects
 * 
 * Why 'by lazy'?
 * - This saves memory, if I never use a repository, it never gets created
 */
class AppContainer(private val context: Context) {

    /**
     * Creates the Room database
     * 
     * fallbackToDestructiveMigration() means if the database schema changes,
     * Room will delete the old database and create a new one.
     */
    private val database: FitFuelDatabase by lazy {
        Room.databaseBuilder(
            context,
            FitFuelDatabase::class.java,
            FitFuelDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    /**
     * Repository for meal operations
     * I get the DAO from the database and pass it to the repository
     */
    val mealRepository: MealRepository by lazy {
        MealRepository(database.mealDao())
    }

    /**
     * Repository for training session operations
     */
    val trainingSessionRepository: TrainingSessionRepository by lazy {
        TrainingSessionRepository(database.trainingSessionDao())
    }

    /**
     * Repository for grocery item operations
     */
    val groceryItemRepository: GroceryItemRepository by lazy {
        GroceryItemRepository(database.groceryItemDao())
    }

    /**
     * Repository for user profile operations
     */
    val userProfileRepository: UserProfileRepository by lazy {
        UserProfileRepository(database.userProfileDao())
    }
}
