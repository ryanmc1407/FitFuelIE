package com.example.fitfuelie.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.fitfuelie.di.AppContainer
import com.example.fitfuelie.navigation.AppNavigation
import com.example.fitfuelie.navigation.Screen
import com.example.fitfuelie.ui.components.BottomNavigationBar

@Composable
fun MainScreen(appContainer: AppContainer) {
    val navController = rememberNavController()
    val screens = listOf(
        Screen.Dashboard,
        Screen.MealPlanner,
        Screen.TrainingCalendar,
        Screen.GroceryList,
        Screen.Profile
    )

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                screens = screens
            )
        }
    ) { padding ->
        AppNavigation(
            appContainer = appContainer,
            modifier = Modifier.padding(padding)
        )
    }
}
