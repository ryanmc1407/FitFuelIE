package com.example.fitfuelie.data.repository

import com.example.fitfuelie.data.local.dao.MealDao
import com.example.fitfuelie.data.local.entity.Meal
import com.example.fitfuelie.data.model.MealType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*
import org.junit.Assert.*

/**
 * MealRepositoryTest
 * 
 * Unit tests for MealRepository.
 * Tests the repository's CRUD operations and data aggregation functions:
 * - Create: Insert new meals
 * - Read: Retrieve meals by ID, date range, or all meals
 * - Update: Modify existing meals
 * - Delete: Remove meals by entity or ID
 * - Aggregate: Calculate nutrition totals
 * 
 * Uses Mockito to mock the DAO layer, ensuring tests focus on
 * repository logic without database dependencies.
 */
class MealRepositoryTest {

    // Mocked DAO dependency
    @Mock
    private lateinit var mealDao: MealDao

    // System under test
    private lateinit var repository: MealRepository

    @Before
    fun setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this)

        // Create repository with mocked DAO
        repository = MealRepository(mealDao)
    }

    @Test
    fun `test getAllMeals returns all meals from DAO`() = runTest {
        // Given: DAO returns list of meals
        val testMeals = listOf(
            createTestMeal(1, "Breakfast", MealType.BREAKFAST),
            createTestMeal(2, "Lunch", MealType.LUNCH)
        )
        `when`(mealDao.getAllMeals()).thenReturn(flowOf(testMeals))

        // When: Repository fetches all meals
        val result = repository.getAllMeals().first()

        // Then: Result should match DAO data
        assertEquals(2, result.size)
        assertEquals("Breakfast", result[0].name)
        assertEquals("Lunch", result[1].name)
        verify(mealDao).getAllMeals()
    }

    @Test
    fun `test getMealsBetweenDates filters by date range`() = runTest {
        // Given: Date range and meals
        val startDate = Date()
        val endDate = Calendar.getInstance().apply {
            time = startDate
            add(Calendar.DAY_OF_MONTH, 1)
        }.time

        val testMeals = listOf(createTestMeal(1, "Lunch", MealType.LUNCH))
        `when`(mealDao.getMealsBetweenDates(startDate, endDate)).thenReturn(flowOf(testMeals))

        // When: Repository fetches meals in date range
        val result = repository.getMealsBetweenDates(startDate, endDate).first()

        // Then: Result should match filtered data
        assertEquals(1, result.size)
        assertEquals("Lunch", result[0].name)
        verify(mealDao).getMealsBetweenDates(startDate, endDate)
    }

    @Test
    fun `test insertMeal calls DAO insert`() = runTest {
        // Given: A new meal
        val meal = createTestMeal(0, "Dinner", MealType.DINNER)
        `when`(mealDao.insertMeal(meal)).thenReturn(1L)

        // When: Repository inserts meal
        val result = repository.insertMeal(meal)

        // Then: DAO insert should be called and return ID
        assertEquals(1L, result)
        verify(mealDao).insertMeal(meal)
    }

    @Test
    fun `test updateMeal calls DAO update`() = runTest {
        // Given: An existing meal with changes
        val meal = createTestMeal(1, "Updated Meal", MealType.SNACK)

        // When: Repository updates meal
        repository.updateMeal(meal)

        // Then: DAO update should be called
        verify(mealDao).updateMeal(meal)
    }

    @Test
    fun `test deleteMeal calls DAO delete`() = runTest {
        // Given: A meal to delete
        val meal = createTestMeal(1, "To Delete", MealType.BREAKFAST)

        // When: Repository deletes meal
        repository.deleteMeal(meal)

        // Then: DAO delete should be called
        verify(mealDao).deleteMeal(meal)
    }

    @Test
    fun `test deleteMealById calls DAO delete by ID`() = runTest {
        // Given: A meal ID
        val mealId = 5L

        // When: Repository deletes by ID
        repository.deleteMealById(mealId)

        // Then: DAO delete by ID should be called
        verify(mealDao).deleteMealById(mealId)
    }

    @Test
    fun `test getTotalCaloriesBetweenDates aggregates correctly`() = runTest {
        // Given: Date range and calorie total
        val startDate = Date()
        val endDate = Date()
        `when`(mealDao.getTotalCaloriesBetweenDates(startDate, endDate)).thenReturn(flowOf(2000))

        // When: Repository fetches total calories
        val result = repository.getTotalCaloriesBetweenDates(startDate, endDate).first()

        // Then: Result should match aggregated value
        assertEquals(2000, result)
        verify(mealDao).getTotalCaloriesBetweenDates(startDate, endDate)
    }

    @Test
    fun `test getTotalProteinBetweenDates aggregates correctly`() = runTest {
        // Given: Date range and protein total
        val startDate = Date()
        val endDate = Date()
        `when`(mealDao.getTotalProteinBetweenDates(startDate, endDate)).thenReturn(flowOf(150f))

        // When: Repository fetches total protein
        val result = repository.getTotalProteinBetweenDates(startDate, endDate).first()

        // Then: Result should match aggregated value
        assertEquals(150f, result ?: 0f, 0.01f)
        verify(mealDao).getTotalProteinBetweenDates(startDate, endDate)
    }

    // Helper function to create test meals
    private fun createTestMeal(id: Long, name: String, type: MealType) = Meal(
        id = id,
        name = name,
        type = type,
        date = Date(),
        calories = 500,
        protein = 30f,
        carbs = 50f,
        fat = 20f,
        notes = "Test meal"
    )
}
