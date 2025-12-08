package com.example.fitfuelie.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fitfuelie.di.AppContainer
import com.example.fitfuelie.ui.screens.*
import com.example.fitfuelie.ui.viewmodel.*

/**
 * AppNavigation
 * 
 * This is the main navigation component for my app. It sets up all the screens
 * and connects them together so users can move between different parts of the app.
 * 
 * How navigation works:
 * - NavHost is like a container that holds one screen at a time
 *  NavHost swaps out the current screen for a new one
 * - Each screen is defined with a 'composable' function
 * 
 *  ViewModels here?
 * - Each screen needs its own ViewModel instance
 * - ViewModels are created when the screen is first shown
 * - I pass repositories from AppContainer so ViewModels can access data
 * 
 * Screens in my app:
 * - Dashboard: Overview of nutrition, training, and steps
 * - MealPlanner: Plan and track meals
 * - TrainingCalendar: Schedule and track workouts
 * - GroceryList: Manage shopping list
 * - Profile: User settings and preferences
 * 
 * @param appContainer Dependency injection container, provides repositories to ViewModels
 * @param navController Controls navigation, tells NavHost which screen to show
 * @param modifier Optional styling modifier for the NavHost
 */

@Composable
fun AppNavigation(
    appContainer: AppContainer,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Create SensorViewModel once for the entire navigation graph
    // This ensures sensor data is consistent across navigation
    val sensorViewModel = SensorViewModel(context)

    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            // Create DashboardViewModel with required repositories
            val viewModel = DashboardViewModel(
                appContainer.mealRepository,
                appContainer.trainingSessionRepository,
                appContainer.userProfileRepository
            )

            DashboardScreen(
                viewModel = viewModel,
                sensorViewModel = sensorViewModel,
                onAddMeal = { navController.navigate(Screen.MealPlanner.route) },
                onAddTraining = { navController.navigate(Screen.TrainingCalendar.route) },
                onViewMeals = { navController.navigate(Screen.MealPlanner.route) },
                onViewTraining = { navController.navigate(Screen.TrainingCalendar.route) }
            )
        }

        composable(Screen.MealPlanner.route) {
            val viewModel = MealPlannerViewModel(
                appContainer.mealRepository,
                appContainer.groceryItemRepository
            )

            MealPlannerScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.TrainingCalendar.route) {
            val viewModel = TrainingCalendarViewModel(
                appContainer.trainingSessionRepository,
                appContainer.userProfileRepository
            )

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
