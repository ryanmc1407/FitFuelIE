package com.example.fitfuelie.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitfuelie.di.AppContainer
import com.example.fitfuelie.ui.screens.*
import com.example.fitfuelie.ui.viewmodel.*

@Composable
fun AppNavigation(
    appContainer: AppContainer,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            val viewModel = DashboardViewModel(
                appContainer.mealRepository,
                appContainer.trainingSessionRepository,
                appContainer.userProfileRepository
            )

            DashboardScreen(
                viewModel = viewModel,
                onAddMeal = { navController.navigate(Screen.MealPlanner.route) },
                onAddTraining = { navController.navigate(Screen.TrainingCalendar.route) },
                onViewMeals = { navController.navigate(Screen.MealPlanner.route) },
                onViewTraining = { navController.navigate(Screen.TrainingCalendar.route) }
            )
        }

        composable(Screen.MealPlanner.route) {
            val viewModel = MealPlannerViewModel(appContainer.mealRepository)

            MealPlannerScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TrainingCalendar.route) {
            val viewModel = TrainingCalendarViewModel(appContainer.trainingSessionRepository)

            TrainingCalendarScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.GroceryList.route) {
            val viewModel = GroceryListViewModel(appContainer.groceryItemRepository)

            GroceryListScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            val viewModel = ProfileViewModel(appContainer.userProfileRepository)

            ProfileScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
