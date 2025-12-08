package com.example.fitfuelie.data.repository

import com.example.fitfuelie.data.local.dao.MealDao
import com.example.fitfuelie.data.local.entity.Meal
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * MealRepository
 * 
 * This is the repository pattern, it's like a middleman between the UI and the database.
 * 
 * Why use a repository?
 * - It keeps the ViewModel clean, ViewModels don't need to know about DAOs
 * - If I later want to add network calls or caching, I can do it here without changing the ViewModel
 * - It makes testing easier, I can mock the repository instead of the whole database
 */
class MealRepository(
    private val mealDao: MealDao  // I inject the DAO, this is dependency injection
) {


    fun getAllMeals(): Flow<List<Meal>> = mealDao.getAllMeals()

    /**
      Gets meals within a date range
    */
    fun getMealsBetweenDates(startDate: Date, endDate: Date): Flow<List<Meal>> =
        mealDao.getMealsBetweenDates(startDate, endDate)

    /**
     * Gets a single meal by Id
     */
    suspend fun getMealById(id: Long): Meal? = mealDao.getMealById(id)


    suspend fun insertMeal(meal: Meal): Long = mealDao.insertMeal(meal)


    suspend fun updateMeal(meal: Meal) = mealDao.updateMeal(meal)


    suspend fun deleteMeal(meal: Meal) = mealDao.deleteMeal(meal)


    suspend fun deleteMealById(id: Long) = mealDao.deleteMealById(id)

    /**
     * Calculates total calories for meals in a date range
     */
    fun getTotalCaloriesBetweenDates(startDate: Date, endDate: Date): Flow<Int?> =
        mealDao.getTotalCaloriesBetweenDates(startDate, endDate)

    /**
     * Calculates total protein for meals in a date range
     */
    fun getTotalProteinBetweenDates(startDate: Date, endDate: Date): Flow<Float?> =
        mealDao.getTotalProteinBetweenDates(startDate, endDate)

    /**
     * Calculates total carbs for meals in a date range
     */
    fun getTotalCarbsBetweenDates(startDate: Date, endDate: Date): Flow<Float?> =
        mealDao.getTotalCarbsBetweenDates(startDate, endDate)

    /**
     * Calculates total fat for meals in a date range
     */
    fun getTotalFatBetweenDates(startDate: Date, endDate: Date): Flow<Float?> =
        mealDao.getTotalFatBetweenDates(startDate, endDate)
}
