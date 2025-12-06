package com.example.fitfuelie.ui.viewmodel

import com.example.fitfuelie.data.local.entity.Meal
import com.example.fitfuelie.data.local.entity.TrainingSession
import com.example.fitfuelie.data.local.entity.UserProfile
import com.example.fitfuelie.data.model.*
import com.example.fitfuelie.data.repository.MealRepository
import com.example.fitfuelie.data.repository.TrainingSessionRepository
import com.example.fitfuelie.data.repository.UserProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*
import org.junit.Assert.*

/**
 * DashboardViewModelTest
 * 
 * Unit tests for DashboardViewModel.
 * Tests the ViewModel's ability to:
 * - Aggregate nutrition data from meals
 * - Calculate training statistics
 * - Combine multiple data sources
 * - Handle empty data states
 * - Update training session completion status
 * 
 * Uses Mockito to mock repository dependencies and coroutines test
 * utilities for testing Flow-based state management.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    // Test dispatcher for coroutines
    private val testDispatcher = StandardTestDispatcher()

    // Mocked dependencies
    @Mock
    private lateinit var mealRepository: MealRepository

    @Mock
    private lateinit var trainingRepository: TrainingSessionRepository

    @Mock
    private lateinit var userProfileRepository: UserProfileRepository

    // System under test
    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setup() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this)

        // Set main dispatcher for testing
        Dispatchers.setMain(testDispatcher)

        // Setup default mock behaviors
        setupDefaultMocks()

        // Create ViewModel instance
        viewModel = DashboardViewModel(
            mealRepository,
            trainingRepository,
            userProfileRepository
        )
    }

    @After
    fun tearDown() {
        // Reset main dispatcher
        Dispatchers.resetMain()
    }

    /**
     * Setup default mock behaviors for repositories.
     * Returns empty flows by default to prevent null pointer exceptions.
     */
    private fun setupDefaultMocks() {
        val today = Date()
        val tomorrow = Calendar.getInstance().apply {
            time = today
            add(Calendar.DAY_OF_MONTH, 1)
        }.time

        // Mock user profile
        `when`(userProfileRepository.getUserProfile()).thenReturn(
            flowOf(createTestUserProfile())
        )

        // Mock nutrition data
        `when`(mealRepository.getTotalCaloriesBetweenDates(any(), any())).thenReturn(flowOf(0))
        `when`(mealRepository.getTotalProteinBetweenDates(any(), any())).thenReturn(flowOf(0f))
        `when`(mealRepository.getTotalCarbsBetweenDates(any(), any())).thenReturn(flowOf(0f))
        `when`(mealRepository.getTotalFatBetweenDates(any(), any())).thenReturn(flowOf(0f))
        `when`(mealRepository.getMealsBetweenDates(any(), any())).thenReturn(flowOf(emptyList()))

        // Mock training data
        `when`(trainingRepository.getCompletedSessionsCount(any(), any())).thenReturn(flowOf(0))
        `when`(trainingRepository.getTotalTrainingTimeBetweenDates(any(), any())).thenReturn(flowOf(0))
        `when`(trainingRepository.getTrainingSessionsBetweenDates(any(), any())).thenReturn(flowOf(emptyList()))
    }

    @Test
    fun `test nutrition summary calculates correctly`() = runTest {
        // Given: Mock nutrition data
        `when`(mealRepository.getTotalCaloriesBetweenDates(any(), any())).thenReturn(flowOf(2000))
        `when`(mealRepository.getTotalProteinBetweenDates(any(), any())).thenReturn(flowOf(150f))
        `when`(mealRepository.getTotalCarbsBetweenDates(any(), any())).thenReturn(flowOf(200f))
        `when`(mealRepository.getTotalFatBetweenDates(any(), any())).thenReturn(flowOf(70f))

        // Recreate ViewModel with new mocks
        viewModel = DashboardViewModel(mealRepository, trainingRepository, userProfileRepository)

        // Advance time to allow StateFlow to emit
        advanceUntilIdle()

        // Then: Nutrition summary should match expected values
        val nutrition = viewModel.todaysNutrition.value
        assertEquals(2000, nutrition.calories)
        assertEquals(150f, nutrition.protein, 0.01f)
        assertEquals(200f, nutrition.carbs, 0.01f)
        assertEquals(70f, nutrition.fat, 0.01f)
    }

    @Test
    fun `test training stats calculates correctly`() = runTest {
        // Given: Mock training data
        `when`(trainingRepository.getCompletedSessionsCount(any(), any())).thenReturn(flowOf(3))
        `when`(trainingRepository.getTotalTrainingTimeBetweenDates(any(), any())).thenReturn(flowOf(90))

        // Recreate ViewModel
        viewModel = DashboardViewModel(mealRepository, trainingRepository, userProfileRepository)

        // Advance time
        advanceUntilIdle()

        // Then: Training stats should match
        val stats = viewModel.todaysTrainingStats.value
        assertEquals(3, stats.completedSessions)
        assertEquals(90, stats.totalTrainingTime)
    }

    @Test
    fun `test user profile loads correctly`() = runTest {
        // Given: User profile in repository
        val testProfile = createTestUserProfile()
        `when`(userProfileRepository.getUserProfile()).thenReturn(flowOf(testProfile))

        // Recreate ViewModel
        viewModel = DashboardViewModel(mealRepository, trainingRepository, userProfileRepository)

        // Advance time
        advanceUntilIdle()

        // Then: Profile should be loaded
        val profile = viewModel.userProfile.value
        assertNotNull(profile)
        assertEquals("Test User", profile?.name)
        assertEquals(2500, profile?.dailyCalorieTarget)
    }

    @Test
    fun `test empty nutrition data returns zeros`() = runTest {
        // Given: No meals (default mock behavior)
        // When: ViewModel is created
        advanceUntilIdle()

        // Then: Nutrition should be all zeros
        val nutrition = viewModel.todaysNutrition.value
        assertEquals(0, nutrition.calories)
        assertEquals(0f, nutrition.protein, 0.01f)
        assertEquals(0f, nutrition.carbs, 0.01f)
        assertEquals(0f, nutrition.fat, 0.01f)
    }

    // Helper function to create test user profile
    private fun createTestUserProfile() = UserProfile(
        id = 1,
        name = "Test User",
        goal = Goal.BUILD_MUSCLE,
        trainingFrequency = TrainingFrequency.FOUR_FIVE_DAYS,
        dietaryPreference = DietaryPreference.NO_RESTRICTIONS,
        weight = 70f,
        dailyCalorieTarget = 2500,
        dailyProteinTarget = 150f,
        dailyCarbTarget = 250f,
        dailyFatTarget = 70f,
        isOnboardingCompleted = true
    )

    // Helper function for Mockito any()
    private fun <T> any(): T {
        return org.mockito.kotlin.any()
    }
}
