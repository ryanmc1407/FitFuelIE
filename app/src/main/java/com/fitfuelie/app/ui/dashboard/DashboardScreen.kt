package com.fitfuelie.app.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.fitfuelie.app.navigation.Screen
import com.fitfuelie.app.ui.mealplanner.DailyNutritionSummary
import com.fitfuelie.app.ui.mealplanner.MealPlannerViewModel
import com.fitfuelie.app.ui.mealplanner.MealPlannerScreen
import com.fitfuelie.app.ui.training.TrainingViewModel
import com.fitfuelie.app.ui.training.TrainingCalendarScreen
import com.fitfuelie.app.ui.grocery.GroceryListScreen
import com.fitfuelie.app.ui.profile.ProfileScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DashboardScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeDashboard()
            }
            composable(BottomNavItem.Meals.route) {
                MealPlannerScreen()
            }
            composable(BottomNavItem.Training.route) {
                TrainingCalendarScreen()
            }
            composable(BottomNavItem.Grocery.route) {
                GroceryListScreen()
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }
        }
    }
}

sealed class BottomNavItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : BottomNavItem("home", Icons.Filled.Home, "Home")
    object Meals : BottomNavItem("meals", Icons.Filled.Restaurant, "Meals")
    object Training : BottomNavItem("training", Icons.Filled.FitnessCenter, "Training")
    object Grocery : BottomNavItem("grocery", Icons.Filled.ShoppingCart, "Grocery")
    object Profile : BottomNavItem("profile", Icons.Filled.Person, "Profile")
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Meals,
        BottomNavItem.Training,
        BottomNavItem.Grocery,
        BottomNavItem.Profile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun HomeDashboard() {
    val context = LocalContext.current
    val mealViewModel: MealPlannerViewModel = viewModel {
        val database = DatabaseProvider.getDatabase(context)
        val repository = MealRepository(database.mealDao())
        MealPlannerViewModel(repository)
    }
    val trainingViewModel: TrainingViewModel = viewModel {
        val database = DatabaseProvider.getDatabase(context)
        val repository = TrainingSessionRepository(database.trainingSessionDao())
        TrainingViewModel(repository)
    }

    val nutritionSummary by mealViewModel.dailyNutritionSummary.collectAsState(
        initial = DailyNutritionSummary(0, 0.0, 0.0, 0.0)
    )
    val mealsCount by mealViewModel.mealsForSelectedDate.collectAsState(initial = emptyList())
    val sessionStats by trainingViewModel.completedSessionsCount.collectAsState(initial = Pair(0, 0))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Welcome to FitFuel IE",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Your personalized nutrition and training companion for GAA athletes",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Quick stats/overview cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickStatCard(
                title = "Today's Meals",
                value = "${mealsCount.size}",
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                title = "Training",
                value = "${sessionStats.first}/${sessionStats.second}",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickStatCard(
                title = "Calories",
                value = "${nutritionSummary.calories}",
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                title = "Protein",
                value = "${nutritionSummary.protein}g",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Quick action buttons
        QuickActionButton(
            title = "Log Meal",
            icon = Icons.Filled.Add,
            onClick = { /* TODO: Navigate to add meal */ }
        )

        QuickActionButton(
            title = "Schedule Training",
            icon = Icons.Filled.Schedule,
            onClick = { /* TODO: Navigate to training calendar */ }
        )

        QuickActionButton(
            title = "Generate Grocery List",
            icon = Icons.Filled.List,
            onClick = { /* TODO: Navigate to grocery list */ }
        )
    }
}

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun QuickActionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(title)
        }
    }
}
