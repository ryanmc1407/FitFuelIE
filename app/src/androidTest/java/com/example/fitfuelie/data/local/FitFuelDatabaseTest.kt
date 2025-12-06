package com.example.fitfuelie.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fitfuelie.data.local.dao.MealDao
import com.example.fitfuelie.data.local.entity.Meal
import com.example.fitfuelie.data.model.MealType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import org.junit.Assert.*

/**
 * FitFuelDatabaseTest
 * 
 * Instrumentation tests for the Room database.
 * Tests run on an Android device or emulator to verify:
 * - Database creation and schema
 * - DAO operations (insert, query, update, delete)
 * - Type converters (Date, Enum conversions)
 * - Query correctness (date ranges, aggregations)
 * - Database constraints and relationships
 * 
 * Uses an in-memory database for fast, isolated testing.
 * Database is created before each test and closed after.
 */
@RunWith(AndroidJUnit4::class)
class FitFuelDatabaseTest {

    private lateinit var database: FitFuelDatabase
    private lateinit var mealDao: MealDao

    @Before
    fun createDb() {
        // Create in-memory database for testing
        // Data is cleared when database is closed
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            FitFuelDatabase::class.java
        ).build()

        mealDao = database.mealDao()
    }

    @After
    fun closeDb() {
        // Close database after each test
        database.close()
    }

    @Test
    fun testInsertAndRetrieveMeal() = runTest {
        // Given: A new meal
        val meal = createTestMeal("Breakfast", MealType.BREAKFAST, 500)

        // When: Insert meal into database
        val id = mealDao.insertMeal(meal)

        // Then: Meal should be retrievable by ID
        val retrieved = mealDao.getMealById(id)
        assertNotNull(retrieved)
        assertEquals("Breakfast", retrieved?.name)
        assertEquals(MealType.BREAKFAST, retrieved?.type)
        assertEquals(500, retrieved?.calories)
    }

    @Test
    fun testGetAllMeals() = runTest {
        // Given: Multiple meals in database
        mealDao.insertMeal(createTestMeal("Breakfast", MealType.BREAKFAST, 400))
        mealDao.insertMeal(createTestMeal("Lunch", MealType.LUNCH, 600))
        mealDao.insertMeal(createTestMeal("Dinner", MealType.DINNER, 700))

        // When: Retrieve all meals
        val allMeals = mealDao.getAllMeals().first()

        // Then: Should return all 3 meals
        assertEquals(3, allMeals.size)
    }

    @Test
    fun testUpdateMeal() = runTest {
        // Given: A meal in the database
        val meal = createTestMeal("Original", MealType.BREAKFAST, 500)
        val id = mealDao.insertMeal(meal)

        // When: Update the meal
        val updatedMeal = meal.copy(
            id = id,
            name = "Updated",
            calories = 600
        )
        mealDao.updateMeal(updatedMeal)

        // Then: Retrieved meal should have updated values
        val retrieved = mealDao.getMealById(id)
        assertEquals("Updated", retrieved?.name)
        assertEquals(600, retrieved?.calories)
    }

    @Test
    fun testDeleteMeal() = runTest {
        // Given: A meal in the database
        val meal = createTestMeal("To Delete", MealType.SNACK, 200)
        val id = mealDao.insertMeal(meal)

        // When: Delete the meal
        mealDao.deleteMealById(id)

        // Then: Meal should no longer exist
        val retrieved = mealDao.getMealById(id)
        assertNull(retrieved)
    }

    @Test
    fun testGetMealsBetweenDates() = runTest {
        // Given: Meals on different dates
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val yesterday = Calendar.getInstance().apply {
            time = today
            add(Calendar.DAY_OF_MONTH, -1)
        }.time

        val tomorrow = Calendar.getInstance().apply {
            time = today
            add(Calendar.DAY_OF_MONTH, 1)
        }.time

        // Insert meals on different days
        mealDao.insertMeal(createTestMeal("Yesterday", MealType.BREAKFAST, 400, yesterday))
        mealDao.insertMeal(createTestMeal("Today", MealType.LUNCH, 500, today))
        mealDao.insertMeal(createTestMeal("Tomorrow", MealType.DINNER, 600, tomorrow))

        // When: Query meals for today only
        val startOfToday = Calendar.getInstance().apply {
            time = today
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val endOfToday = Calendar.getInstance().apply {
            time = startOfToday
            add(Calendar.DAY_OF_MONTH, 1)
        }.time

        val todaysMeals = mealDao.getMealsBetweenDates(startOfToday, endOfToday).first()

        // Then: Should only return today's meal
        assertEquals(1, todaysMeals.size)
        assertEquals("Today", todaysMeals[0].name)
    }

    @Test
    fun testGetTotalCaloriesBetweenDates() = runTest {
        // Given: Multiple meals with known calorie totals
        val today = Date()
        val tomorrow = Calendar.getInstance().apply {
            time = today
            add(Calendar.DAY_OF_MONTH, 1)
        }.time

        mealDao.insertMeal(createTestMeal("Meal 1", MealType.BREAKFAST, 400, today))
        mealDao.insertMeal(createTestMeal("Meal 2", MealType.LUNCH, 600, today))
        mealDao.insertMeal(createTestMeal("Meal 3", MealType.DINNER, 500, today))

        // When: Calculate total calories
        val totalCalories = mealDao.getTotalCaloriesBetweenDates(today, tomorrow).first()

        // Then: Should sum all calories
        assertEquals(1500, totalCalories)
    }

    @Test
    fun testGetTotalProteinBetweenDates() = runTest {
        // Given: Multiple meals with known protein totals
        val today = Date()
        val tomorrow = Calendar.getInstance().apply {
            time = today
            add(Calendar.DAY_OF_MONTH, 1)
        }.time

        mealDao.insertMeal(createTestMeal("Meal 1", MealType.BREAKFAST, 400, today, protein = 30f))
        mealDao.insertMeal(createTestMeal("Meal 2", MealType.LUNCH, 600, today, protein = 40f))
        mealDao.insertMeal(createTestMeal("Meal 3", MealType.DINNER, 500, today, protein = 50f))

        // When: Calculate total protein
        val totalProtein = mealDao.getTotalProteinBetweenDates(today, tomorrow).first()

        // Then: Should sum all protein
        assertEquals(120f, totalProtein, 0.01f)
    }

    @Test
    fun testTypeConvertersForDate() = runTest {
        // Given: A meal with a specific date
        val specificDate = Calendar.getInstance().apply {
            set(2024, Calendar.JANUARY, 15, 12, 30, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val meal = createTestMeal("Test", MealType.LUNCH, 500, specificDate)

        // When: Insert and retrieve
        val id = mealDao.insertMeal(meal)
        val retrieved = mealDao.getMealById(id)

        // Then: Date should be preserved correctly
        assertNotNull(retrieved)
        assertEquals(specificDate.time, retrieved?.date?.time)
    }

    @Test
    fun testTypeConvertersForEnum() = runTest {
        // Given: Meals with different meal types
        val breakfast = createTestMeal("Breakfast", MealType.BREAKFAST, 400)
        val lunch = createTestMeal("Lunch", MealType.LUNCH, 600)
        val dinner = createTestMeal("Dinner", MealType.DINNER, 700)
        val snack = createTestMeal("Snack", MealType.SNACK, 200)

        // When: Insert and retrieve
        val breakfastId = mealDao.insertMeal(breakfast)
        val lunchId = mealDao.insertMeal(lunch)
        val dinnerId = mealDao.insertMeal(dinner)
        val snackId = mealDao.insertMeal(snack)

        // Then: Enum values should be preserved
        assertEquals(MealType.BREAKFAST, mealDao.getMealById(breakfastId)?.type)
        assertEquals(MealType.LUNCH, mealDao.getMealById(lunchId)?.type)
        assertEquals(MealType.DINNER, mealDao.getMealById(dinnerId)?.type)
        assertEquals(MealType.SNACK, mealDao.getMealById(snackId)?.type)
    }

    // Helper function to create test meals
    private fun createTestMeal(
        name: String,
        type: MealType,
        calories: Int,
        date: Date = Date(),
        protein: Float = 30f
    ) = Meal(
        id = 0,
        name = name,
        type = type,
        date = date,
        calories = calories,
        protein = protein,
        carbs = 50f,
        fat = 20f,
        notes = "Test meal"
    )
}
