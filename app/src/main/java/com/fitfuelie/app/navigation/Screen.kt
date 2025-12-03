package com.fitfuelie.app.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object MealPlanner : Screen("meal_planner")
    object TrainingCalendar : Screen("training_calendar")
    object GroceryList : Screen("grocery_list")
    object Profile : Screen("profile")
}
