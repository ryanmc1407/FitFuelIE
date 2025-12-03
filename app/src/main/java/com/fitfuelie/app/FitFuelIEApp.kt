 package com.fitfuelie.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fitfuelie.app.data.DatabaseProvider
import com.fitfuelie.app.data.repository.UserProfileRepository
import com.fitfuelie.app.navigation.Screen
import com.fitfuelie.app.ui.onboarding.OnboardingScreen
import com.fitfuelie.app.ui.onboarding.OnboardingViewModel
import com.fitfuelie.app.ui.dashboard.DashboardScreen

@Composable
fun FitFuelIEApp() {
    val context = LocalContext.current
    val onboardingViewModel: OnboardingViewModel = viewModel {
        val database = DatabaseProvider.getDatabase(context)
        val repository = UserProfileRepository(database.userProfileDao())
        OnboardingViewModel(repository)
    }
    val navController = rememberNavController()

    // Check if onboarding is complete
    val isOnboardingComplete by onboardingViewModel.isOnboardingComplete().collectAsState(initial = false)

    val startDestination = if (isOnboardingComplete) Screen.Dashboard.route else Screen.Onboarding.route

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen()
        }
    }
}
