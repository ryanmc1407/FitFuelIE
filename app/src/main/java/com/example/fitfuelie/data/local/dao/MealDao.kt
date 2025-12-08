package com.example.fitfuelie.data.local.dao

import androidx.room.*
import com.example.fitfuelie.data.local.entity.Meal
import kotlinx.coroutines.flow.Flow
import java.util.Date

/**
 * MealDao (Data Access Object)
 * 
 * This interface defines all the database operations I can do with meals.
 * Room automatically creates the actual implementation for me, I just write the interface
 * 
 * I use Flow here because it's reactive, when data changes in the database,
 * the UI automatically updates without me having to manually refresh.
 */
@Dao
interface MealDao {

    /**
     * Gets all meals from the database, sorted by date (newest first)
     * Returns a Flow so the UI updates automatically when meals are added/removed
     */
    @Query("SELECT * FROM meals ORDER BY date DESC")
    fun getAllMeals(): Flow<List<Meal>>

    /**
     * Gets meals within a date range, useful for showing daily/weekly summaries
     * The :startDate and :endDate are parameters I pass in (Room handles the SQL injection protection)
     */
    @Query("SELECT * FROM meals WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC")
    fun getMealsBetweenDates(startDate: Date, endDate: Date): Flow<List<Meal>>

    /**
     * Gets a single meal by its ID
     * suspend means this can pause/resume, it won't block the UI thread
     * Returns Meal (nullable) because the meal might not exist
     */
    @Query("SELECT * FROM meals WHERE id = :id")
    suspend fun getMealById(id: Long): Meal?

    /**
     * Inserts a new meal into the database
     * Returns the ID that was generated for the new meal
     * REPLACE means if a meal with the same ID exists, it gets replaced
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal): Long

    /**
     * Updates an existing meal
     * Room matches by primary key (id) and updates all the other fields
     */
    @Update
    suspend fun updateMeal(meal: Meal)

    /**
     * Deletes a meal from the database
     */
    @Delete
    suspend fun deleteMeal(meal: Meal)

    /**
     * Deletes a meal by ID
     */
    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteMealById(id: Long)

    /**
     * Calculates total calories for meals in a date range
     * I do this in SQL because it's Way faster than loading all meals into memory and adding them up
     * Returns Int? because if there are no meals, SUM returns null
     */
    @Query("SELECT SUM(calories) FROM meals WHERE date >= :startDate AND date <= :endDate")
    fun getTotalCaloriesBetweenDates(startDate: Date, endDate: Date): Flow<Int?>

    /**
     * Calculates total protein for meals in a date range
     *  let the database do the maths
     */
    @Query("SELECT SUM(protein) FROM meals WHERE date >= :startDate AND date <= :endDate")
    fun getTotalProteinBetweenDates(startDate: Date, endDate: Date): Flow<Float?>

    /**
     * Calculates total carbs for meals in a date range
     */
    @Query("SELECT SUM(carbs) FROM meals WHERE date >= :startDate AND date <= :endDate")
    fun getTotalCarbsBetweenDates(startDate: Date, endDate: Date): Flow<Float?>

    /**
     * Calculates total fat for meals in a date range
     */
    @Query("SELECT SUM(fat) FROM meals WHERE date >= :startDate AND date <= :endDate")
    fun getTotalFatBetweenDates(startDate: Date, endDate: Date): Flow<Float?>
}
