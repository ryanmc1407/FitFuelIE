package com.example.fitfuelie.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sports
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Screen Sealed Class
 * 
 * This defines all the screens in my app and their properties.
 *
 * - It's like an enum, but more powerful, each screen can have different properties
 * - The compiler knows all possible screens, so it helps catch bugs
 * - I can't accidentally create a screen that doesn't exist
 */
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Home", Icons.Default.Home)
    object MealPlanner : Screen("meal_planner", "Meals", Icons.Default.Restaurant)
    object TrainingCalendar : Screen("training_calendar", "Training", Icons.Default.Sports)
    object GroceryList : Screen("grocery_list", "Grocery", Icons.Default.ShoppingCart)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
}
