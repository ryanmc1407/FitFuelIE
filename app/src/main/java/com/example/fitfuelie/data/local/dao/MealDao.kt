package com.example.fitfuelie.data.local.dao

import androidx.room.*
import com.example.fitfuelie.data.local.entity.Meal
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface MealDao {

    @Query("SELECT * FROM meals ORDER BY date DESC")
    fun getAllMeals(): Flow<List<Meal>>

    @Query("SELECT * FROM meals WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getMealsBetweenDates(startDate: Date, endDate: Date): Flow<List<Meal>>

    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getMealById(id: Long): Meal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal): Long

    @Update
    suspend fun updateMeal(meal: Meal)

    @Delete
    suspend fun deleteMeal(meal: Meal)

    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteMealById(id: Long)

    @Query("SELECT SUM(calories) FROM meals WHERE date >= :startDate AND date <= :endDate")
    fun getTotalCaloriesBetweenDates(startDate: Date, endDate: Date): Flow<Int?>

    @Query("SELECT SUM(protein) FROM meals WHERE date >= :startDate AND date <= :endDate")
    fun getTotalProteinBetweenDates(startDate: Date, endDate: Date): Flow<Float?>

    @Query("SELECT SUM(carbs) FROM meals WHERE date >= :startDate AND date <= :endDate")
    fun getTotalCarbsBetweenDates(startDate: Date, endDate: Date): Flow<Float?>

    @Query("SELECT SUM(fat) FROM meals WHERE date >= :startDate AND date <= :endDate")
    fun getTotalFatBetweenDates(startDate: Date, endDate: Date): Flow<Float?>
}
