package com.example.fitfuelie.di

import android.content.Context
import androidx.room.Room
import com.example.fitfuelie.data.local.FitFuelDatabase
import com.example.fitfuelie.data.repository.*

class AppContainer(private val context: Context) {

    // Database
    private val database: FitFuelDatabase by lazy {
        Room.databaseBuilder(
            context,
            FitFuelDatabase::class.java,
            FitFuelDatabase.DATABASE_NAME
        ).build()
    }

    // Repositories
    val mealRepository: MealRepository by lazy {
        MealRepository(database.mealDao())
    }

    val trainingSessionRepository: TrainingSessionRepository by lazy {
        TrainingSessionRepository(database.trainingSessionDao())
    }

    val groceryItemRepository: GroceryItemRepository by lazy {
        GroceryItemRepository(database.groceryItemDao())
    }

    val userProfileRepository: UserProfileRepository by lazy {
        UserProfileRepository(database.userProfileDao())
    }
}
