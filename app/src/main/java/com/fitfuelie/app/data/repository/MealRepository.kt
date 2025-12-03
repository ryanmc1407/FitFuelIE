package com.fitfuelie.app.data.repository

import com.fitfuelie.app.data.dao.MealDao
import com.fitfuelie.app.data.model.Meal
import com.fitfuelie.app.data.model.MealType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepository @Inject constructor(
    private val mealDao: MealDao
) {
    // Create
    suspend fun insertMeal(meal: Meal): Long = mealDao.insertMeal(meal)

    // Read
    fun getAllMeals(): Flow<List<Meal>> = mealDao.getAllMeals()

    fun getMealsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Meal>> =
        mealDao.getMealsForDateRange(startDate, endDate)

    fun getMealsByType(mealType: MealType): Flow<List<Meal>> = mealDao.getMealsByType(mealType)

    suspend fun getMealById(mealId: Long): Meal? = mealDao.getMealById(mealId)

    // Update
    suspend fun updateMeal(meal: Meal) = mealDao.updateMeal(meal)

    // Delete
    suspend fun deleteMeal(meal: Meal) = mealDao.deleteMeal(meal)

    // Nutrition summaries
    fun getTotalCaloriesForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Int?> =
        mealDao.getTotalCaloriesForDateRange(startDate, endDate)

    fun getTotalProteinForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double?> =
        mealDao.getTotalProteinForDateRange(startDate, endDate)

    fun getTotalCarbsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double?> =
        mealDao.getTotalCarbsForDateRange(startDate, endDate)

    fun getTotalFatForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double?> =
        mealDao.getTotalFatForDateRange(startDate, endDate)
}
