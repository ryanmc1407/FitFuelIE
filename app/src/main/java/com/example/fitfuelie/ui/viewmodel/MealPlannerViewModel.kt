package com.example.fitfuelie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfuelie.data.local.entity.Meal
import com.example.fitfuelie.data.model.MealType
import com.example.fitfuelie.data.repository.MealRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

/**
 * MealPlannerViewModel
 * 
 * Manages meal planning and tracking. Users can add, edit, and delete meals.
 * Also calculates daily nutrition totals for the selected date.
 */
class MealPlannerViewModel(
    private val mealRepository: MealRepository,
    private val groceryItemRepository: com.example.fitfuelie.data.repository.GroceryItemRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(Calendar.getInstance().time)
    val selectedDate: StateFlow<Date> = _selectedDate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val mealsForSelectedDate = combine(
        _selectedDate,
        mealRepository.getAllMeals()
    ) { selectedDate, allMeals ->
        val startOfDay = Calendar.getInstance().apply {
            time = selectedDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val endOfDay = Calendar.getInstance().apply {
            time = startOfDay
            add(Calendar.DAY_OF_MONTH, 1)
        }.time

        allMeals.filter { meal ->
            meal.date >= startOfDay && meal.date < endOfDay
        }.sortedBy { it.type.ordinal }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dailyNutritionSummary = mealsForSelectedDate.map { meals ->
        NutritionSummary(
            calories = meals.sumOf { it.calories },
            protein = meals.sumOf { it.protein.toDouble() }.toFloat(),
            carbs = meals.sumOf { it.carbs.toDouble() }.toFloat(),
            fat = meals.sumOf { it.fat.toDouble() }.toFloat()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NutritionSummary(0, 0f, 0f, 0f))

    fun selectDate(date: Date) {
        _selectedDate.value = date
    }

    fun addMeal(
        name: String,
        type: MealType,
        calories: Int,
        protein: Float,
        carbs: Float,
        fat: Float,
        notes: String? = null,
        addToGroceryList: Boolean = false
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val meal = Meal(
                    name = name,
                    type = type,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    date = _selectedDate.value,
                    notes = notes
                )
                mealRepository.insertMeal(meal)

                if (addToGroceryList) {
                    val groceryItem = com.example.fitfuelie.data.local.entity.GroceryItem(
                        name = name,
                        quantity = "1",
                        category = com.example.fitfuelie.data.model.GroceryCategory.OTHER,
                        notes = if (notes.isNullOrBlank()) "From Meal Planner" else "From Meal Planner: $notes"
                    )
                    groceryItemRepository.insertGroceryItem(groceryItem)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMeal(meal: Meal) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                mealRepository.updateMeal(meal)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteMeal(meal: Meal) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                mealRepository.deleteMeal(meal)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMealById(id: Long): Flow<Meal?> {
        return flow {
            val meal = mealRepository.getMealById(id)
            emit(meal)
        }
    }
}
