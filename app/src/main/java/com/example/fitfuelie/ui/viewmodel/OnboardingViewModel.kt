package com.example.fitfuelie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfuelie.data.local.entity.Meal
import com.example.fitfuelie.data.local.entity.UserProfile
import com.example.fitfuelie.data.model.DietaryPreference
import com.example.fitfuelie.data.model.Goal
import com.example.fitfuelie.data.model.MealType
import com.example.fitfuelie.data.model.TrainingFrequency
import com.example.fitfuelie.data.repository.MealRepository
import com.example.fitfuelie.data.repository.UserProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

/**
 * OnboardingViewModel
 * 
 * Manages the onboarding flow ,collects user info and calculates nutrition targets.
 * Uses step-by-step flow to gather goal, frequency, weight, and dietary preferences.
 * Generates sample meals based on selected dietary preference.
 */
class OnboardingViewModel(
    private val userProfileRepository: UserProfileRepository,
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()

    private val _selectedGoal = MutableStateFlow<Goal?>(null)
    val selectedGoal: StateFlow<Goal?> = _selectedGoal.asStateFlow()

    private val _selectedTrainingFrequency = MutableStateFlow<TrainingFrequency?>(null)
    val selectedTrainingFrequency: StateFlow<TrainingFrequency?> = _selectedTrainingFrequency.asStateFlow()

    private val _selectedDietaryPreference = MutableStateFlow<DietaryPreference?>(null)
    val selectedDietaryPreference: StateFlow<DietaryPreference?> = _selectedDietaryPreference.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _weight = MutableStateFlow("")
    val weight: StateFlow<String> = _weight.asStateFlow()

    fun selectGoal(goal: Goal) {
        _selectedGoal.value = goal
    }

    fun selectTrainingFrequency(frequency: TrainingFrequency) {
        _selectedTrainingFrequency.value = frequency
    }

    fun selectDietaryPreference(preference: DietaryPreference) {
        _selectedDietaryPreference.value = preference
    }

    fun updateUserName(name: String) {
        _userName.value = name
    }

    fun updateWeight(weightInput: String) {
        // Allow only numbers and one decimal point
        if (weightInput.isEmpty() || weightInput.matches(Regex("^\\d*\\.?\\d*$"))) {
            _weight.value = weightInput
        }
    }

    fun nextStep() {
        if (_currentStep.value < 3) { // Increased steps from 2 to 3
            _currentStep.value += 1
        }
    }

    fun previousStep() {
        if (_currentStep.value > 0) {
            _currentStep.value -= 1
        }
    }

    fun completeOnboarding() {
        val goal = _selectedGoal.value ?: return
        val trainingFrequency = _selectedTrainingFrequency.value ?: return
        val dietaryPreference = _selectedDietaryPreference.value ?: return
        val name = _userName.value.takeIf { it.isNotBlank() } ?: return
        val weightVal = _weight.value.toFloatOrNull() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // Calculate default nutrition targets based on selections
                val (calories, protein, carbs, fat) = calculateDefaultTargets(goal, trainingFrequency, weightVal)

                val userProfile = UserProfile(
                    name = name,
                    goal = goal,
                    trainingFrequency = trainingFrequency,
                    dietaryPreference = dietaryPreference,
                    weight = weightVal,
                    dailyCalorieTarget = calories,
                    dailyProteinTarget = protein,
                    dailyCarbTarget = carbs,
                    dailyFatTarget = fat,
                    isOnboardingCompleted = true
                )

                userProfileRepository.insertUserProfile(userProfile)
                
                // Generate sample meals based on dietary preference
                generateSampleMeals(dietaryPreference)
            } catch (e: Exception) {
                _error.value = "Failed to save profile: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Generates sample meals based on the user's dietary preference.
     * Creates breakfast, lunch, dinner, and snack suggestions.
     */
    private suspend fun generateSampleMeals(dietaryPreference: DietaryPreference) {
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val sampleMeals = when (dietaryPreference) {
            DietaryPreference.VEGETARIAN -> getVegetarianMeals(today)
            DietaryPreference.VEGAN -> getVeganMeals(today)
            DietaryPreference.GLUTEN_FREE -> getGlutenFreeMeals(today)
            DietaryPreference.KETO -> getKetoMeals(today)
            DietaryPreference.NO_RESTRICTIONS -> getRegularMeals(today)
        }

        // Insert all sample meals
        sampleMeals.forEach { meal ->
            mealRepository.insertMeal(meal)
        }
    }

    /**
     * Returns vegetarian meal suggestions (no meat, but includes eggs/dairy)
     */
    private fun getVegetarianMeals(date: Date): List<Meal> = listOf(
        Meal(
            name = "Greek Yogurt with Berries",
            type = MealType.BREAKFAST,
            calories = 250,
            protein = 15f,
            carbs = 30f,
            fat = 8f,
            date = date,
            notes = "Vegetarian - High protein breakfast"
        ),
        Meal(
            name = "Quinoa Salad with Feta",
            type = MealType.LUNCH,
            calories = 450,
            protein = 18f,
            carbs = 55f,
            fat = 15f,
            date = date,
            notes = "Vegetarian - Complete protein source"
        ),
        Meal(
            name = "Vegetable Stir Fry with Tofu",
            type = MealType.DINNER,
            calories = 380,
            protein = 22f,
            carbs = 40f,
            fat = 12f,
            date = date,
            notes = "Vegetarian - Rich in plant protein"
        ),
        Meal(
            name = "Apple with Almond Butter",
            type = MealType.SNACK,
            calories = 200,
            protein = 6f,
            carbs = 25f,
            fat = 10f,
            date = date,
            notes = "Vegetarian - Healthy snack"
        )
    )

    /**
     * Returns vegan meal suggestions (no animal products)
     */
    private fun getVeganMeals(date: Date): List<Meal> = listOf(
        Meal(
            name = "Overnight Oats with Fruits",
            type = MealType.BREAKFAST,
            calories = 320,
            protein = 12f,
            carbs = 55f,
            fat = 8f,
            date = date,
            notes = "Vegan - Plant-based breakfast"
        ),
        Meal(
            name = "Chickpea Curry with Rice",
            type = MealType.LUNCH,
            calories = 480,
            protein = 20f,
            carbs = 70f,
            fat = 10f,
            date = date,
            notes = "Vegan - High protein legume meal"
        ),
        Meal(
            name = "Lentil Bolognese with Pasta",
            type = MealType.DINNER,
            calories = 520,
            protein = 25f,
            carbs = 75f,
            fat = 12f,
            date = date,
            notes = "Vegan - Protein-rich dinner"
        ),
        Meal(
            name = "Hummus with Veggie Sticks",
            type = MealType.SNACK,
            calories = 180,
            protein = 8f,
            carbs = 20f,
            fat = 8f,
            date = date,
            notes = "Vegan - Nutritious snack"
        )
    )

    /**
     * Returns gluten-free meal suggestions
     */
    private fun getGlutenFreeMeals(date: Date): List<Meal> = listOf(
        Meal(
            name = "Scrambled Eggs with Avocado",
            type = MealType.BREAKFAST,
            calories = 350,
            protein = 18f,
            carbs = 8f,
            fat = 28f,
            date = date,
            notes = "Gluten-free - High protein breakfast"
        ),
        Meal(
            name = "Grilled Chicken with Sweet Potato",
            type = MealType.LUNCH,
            calories = 420,
            protein = 35f,
            carbs = 45f,
            fat = 10f,
            date = date,
            notes = "Gluten-free - Balanced meal"
        ),
        Meal(
            name = "Salmon with Quinoa and Vegetables",
            type = MealType.DINNER,
            calories = 480,
            protein = 32f,
            carbs = 40f,
            fat = 18f,
            date = date,
            notes = "Gluten-free - Omega-3 rich"
        ),
        Meal(
            name = "Mixed Nuts and Seeds",
            type = MealType.SNACK,
            calories = 220,
            protein = 8f,
            carbs = 10f,
            fat = 18f,
            date = date,
            notes = "Gluten-free - Healthy fats"
        )
    )

    /**
     * Returns keto meal suggestions (low-carb, high-fat)
     */
    private fun getKetoMeals(date: Date): List<Meal> = listOf(
        Meal(
            name = "Bacon and Eggs",
            type = MealType.BREAKFAST,
            calories = 380,
            protein = 22f,
            carbs = 2f,
            fat = 30f,
            date = date,
            notes = "Keto - High fat, low carb"
        ),
        Meal(
            name = "Cauliflower Rice with Chicken",
            type = MealType.LUNCH,
            calories = 420,
            protein = 40f,
            carbs = 8f,
            fat = 22f,
            date = date,
            notes = "Keto - Low carb alternative"
        ),
        Meal(
            name = "Salmon with Asparagus",
            type = MealType.DINNER,
            calories = 450,
            protein = 35f,
            carbs = 6f,
            fat = 30f,
            date = date,
            notes = "Keto - High fat, high protein"
        ),
        Meal(
            name = "Cheese and Olives",
            type = MealType.SNACK,
            calories = 200,
            protein = 10f,
            carbs = 3f,
            fat = 16f,
            date = date,
            notes = "Keto - Perfect keto snack"
        )
    )

    /**
     * Returns regular meal suggestions (no restrictions)
     */
    private fun getRegularMeals(date: Date): List<Meal> = listOf(
        Meal(
            name = "Whole Grain Toast with Eggs",
            type = MealType.BREAKFAST,
            calories = 320,
            protein = 18f,
            carbs = 35f,
            fat = 12f,
            date = date,
            notes = "Balanced breakfast"
        ),
        Meal(
            name = "Grilled Chicken Wrap",
            type = MealType.LUNCH,
            calories = 450,
            protein = 32f,
            carbs = 40f,
            fat = 15f,
            date = date,
            notes = "High protein lunch"
        ),
        Meal(
            name = "Beef Stir Fry with Rice",
            type = MealType.DINNER,
            calories = 520,
            protein = 35f,
            carbs = 55f,
            fat = 16f,
            date = date,
            notes = "Complete dinner"
        ),
        Meal(
            name = "Greek Yogurt with Honey",
            type = MealType.SNACK,
            calories = 180,
            protein = 12f,
            carbs = 22f,
            fat = 4f,
            date = date,
            notes = "Protein-rich snack"
        )
    )

    private fun calculateDefaultTargets(
        goal: Goal, 
        trainingFrequency: TrainingFrequency,
        weightKg: Float
    ): Quadruple<Int, Float, Float, Float> {
        //  Estimate BMR
        // BMR ≈ 22 * weight_in_kg (Rough estimate for average person)
        val bmr = 22 * weightKg

        //  Estimate based on activity
        val activityMultiplier = when (trainingFrequency) {
            TrainingFrequency.TWO_THREE_DAYS -> 1.375f // Lightly active
            TrainingFrequency.FOUR_FIVE_DAYS -> 1.55f  // Moderately active
            TrainingFrequency.SIX_PLUS_DAYS -> 1.725f  // Very active
        }
        
        var targetCalories = (bmr * activityMultiplier).toInt()

        //  Adjust for Goal
        when (goal) {
            Goal.LOSE_WEIGHT -> targetCalories -= 500
            Goal.BUILD_MUSCLE -> targetCalories += 300
            Goal.IMPROVE_PERFORMANCE -> targetCalories += 0 // Maintenance/Performance
            Goal.MAINTAIN_FITNESS -> targetCalories += 0
        }

        // Ensure calories don't go too low
        if (targetCalories < 1200) targetCalories = 1200

        //  Calculate Macros
        // Protein: 2g per kg of bodyweight (good for active individuals)
        val proteinGrams = (2.0f * weightKg).coerceAtLeast(50f) // Minimum 50g
        
        // Fat: 0.8g per kg of bodyweight
        val fatGrams = (0.8f * weightKg).coerceAtLeast(30f) // Minimum 30g

        // Carbs: Remaining calories
        // Protein = 4 cal/g, Fat = 9 cal/g, Carbs = 4 cal/g
        val proteinCals = proteinGrams * 4
        val fatCals = fatGrams * 9
        val remainingCals = targetCalories - proteinCals - fatCals
        val carbGrams = (remainingCals / 4).coerceAtLeast(50f) // Minimum 50g

        return Quadruple(targetCalories, proteinGrams, carbGrams, fatGrams)
    }

    val isNextEnabled: StateFlow<Boolean> = kotlinx.coroutines.flow.combine(
        _currentStep,
        _selectedGoal,
        _selectedTrainingFrequency,
        _selectedDietaryPreference,
        _userName,
        _weight
    ) { args: Array<Any?> ->
        val step = args[0] as Int
        val goal = args[1] as Goal?
        val freq = args[2] as TrainingFrequency?
        val diet = args[3] as DietaryPreference?
        val name = args[4] as String
        val weight = args[5] as String

        when (step) {
            0 -> goal != null
            1 -> freq != null
            2 -> weight.toFloatOrNull() != null && weight.toFloat() > 0
            3 -> diet != null && name.isNotBlank()
            else -> false
        }
    }.stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000), false)

    fun clearError() {
        _error.value = null
    }
}

// Helper data class for returning multiple values
data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
