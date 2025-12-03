package com.example.fitfuelie.data.repository

import com.example.fitfuelie.data.local.dao.MealDao
import com.example.fitfuelie.data.local.entity.Meal
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepository @Inject constructor(
    private val mealDao: MealDao
) {

    fun getAllMeals(): Flow<List<Meal>> = mealDao.getAllMeals()

    fun getMealsBetweenDates(startDate: Date, endDate: Date): Flow<List<Meal>> =
        mealDao.getMealsBetweenDates(startDate, endDate)

    suspend fun getMealById(id: Long): Meal? = mealDao.getMealById(id)

    suspend fun insertMeal(meal: Meal): Long = mealDao.insertMeal(meal)

    suspend fun updateMeal(meal: Meal) = mealDao.updateMeal(meal)

    suspend fun deleteMeal(meal: Meal) = mealDao.deleteMeal(meal)

    suspend fun deleteMealById(id: Long) = mealDao.deleteMealById(id)

    fun getTotalCaloriesBetweenDates(startDate: Date, endDate: Date): Flow<Int?> =
        mealDao.getTotalCaloriesBetweenDates(startDate, endDate)

    fun getTotalProteinBetweenDates(startDate: Date, endDate: Date): Flow<Float?> =
        mealDao.getTotalProteinBetweenDates(startDate, endDate)

    fun getTotalCarbsBetweenDates(startDate: Date, endDate: Date): Flow<Float?> =
        mealDao.getTotalCarbsBetweenDates(startDate, endDate)

    fun getTotalFatBetweenDates(startDate: Date, endDate: Date): Flow<Float?> =
        mealDao.getTotalFatBetweenDates(startDate, endDate)
}
