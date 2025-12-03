package com.fitfuelie.app.ui.mealplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fitfuelie.app.data.DatabaseProvider
import com.fitfuelie.app.data.model.Meal
import com.fitfuelie.app.data.model.MealType
import com.fitfuelie.app.data.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

class MealPlannerViewModel(
    private val mealRepository: MealRepository
) : ViewModel() {

    // UI state
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    // Meals for selected date
    val mealsForSelectedDate: Flow<List<Meal>> = combine(
        mealRepository.getAllMeals(),
        _selectedDate
    ) { meals, date ->
        meals.filter { meal ->
            meal.dateTime.toLocalDate() == date
        }
    }

    // Nutrition summary for selected date
    val dailyNutritionSummary = combine(
        mealRepository.getTotalCaloriesForDateRange(_selectedDate.value, _selectedDate.value),
        mealRepository.getTotalProteinForDateRange(_selectedDate.value, _selectedDate.value),
        mealRepository.getTotalCarbsForDateRange(_selectedDate.value, _selectedDate.value),
        mealRepository.getTotalFatForDateRange(_selectedDate.value, _selectedDate.value)
    ) { calories, protein, carbs, fat ->
        DailyNutritionSummary(
            calories = calories ?: 0,
            protein = protein ?: 0.0,
            carbs = carbs ?: 0.0,
            fat = fat ?: 0.0
        )
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun addMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.insertMeal(meal)
        }
    }

    fun updateMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.updateMeal(meal)
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            mealRepository.deleteMeal(meal)
        }
    }

    suspend fun getMealById(mealId: Long): Meal? {
        return mealRepository.getMealById(mealId)
    }
}

data class DailyNutritionSummary(
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)
