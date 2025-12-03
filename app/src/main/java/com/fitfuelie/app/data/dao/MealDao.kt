package com.fitfuelie.app.data.dao

import androidx.room.*
import com.fitfuelie.app.data.model.Meal
import com.fitfuelie.app.data.model.MealType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal): Long

    @Update
    suspend fun updateMeal(meal: Meal)

    @Delete
    suspend fun deleteMeal(meal: Meal)

    @Query("SELECT * FROM meals WHERE id = :mealId")
    suspend fun getMealById(mealId: Long): Meal?

    @Query("SELECT * FROM meals ORDER BY dateTime DESC")
    fun getAllMeals(): Flow<List<Meal>>

    @Query("SELECT * FROM meals WHERE dateTime >= :startDate AND dateTime < :endDate ORDER BY dateTime DESC")
    fun getMealsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Meal>>

    @Query("SELECT * FROM meals WHERE mealType = :mealType ORDER BY dateTime DESC")
    fun getMealsByType(mealType: MealType): Flow<List<Meal>>

    @Query("SELECT SUM(calories) FROM meals WHERE dateTime >= :startDate AND dateTime < :endDate")
    fun getTotalCaloriesForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Int?>

    @Query("SELECT SUM(protein) FROM meals WHERE dateTime >= :startDate AND dateTime < :endDate")
    fun getTotalProteinForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double?>

    @Query("SELECT SUM(carbs) FROM meals WHERE dateTime >= :startDate AND dateTime < :endDate")
    fun getTotalCarbsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double?>

    @Query("SELECT SUM(fat) FROM meals WHERE dateTime >= :startDate AND dateTime < :endDate")
    fun getTotalFatForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<Double?>
}
