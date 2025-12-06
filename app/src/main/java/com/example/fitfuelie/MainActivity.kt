package com.example.fitfuelie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitfuelie.di.AppContainer
import com.example.fitfuelie.ui.screens.MainScreen
import com.example.fitfuelie.ui.screens.OnboardingScreen
import com.example.fitfuelie.ui.theme.FitFuelIETheme
import com.example.fitfuelie.ui.viewmodel.MainViewModel

import com.example.fitfuelie.ui.viewmodel.OnboardingViewModel

/**
 * MainActivity
 * 
 * Main entry point for the FitFuel app.
 * Responsible for:
 * - Initializing the app container (dependency injection)
 * - Setting up the app theme
 * - Determining whether to show onboarding or main app
 * 
 * The activity uses edge-to-edge display for a modern, immersive UI.
 */
class MainActivity : ComponentActivity() {

    // App-wide dependency injection container
    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize dependency injection container
        appContainer = AppContainer(this)
        
        // Enable edge-to-edge display (content extends behind system bars)
        enableEdgeToEdge()

        setContent {
            FitFuelIETheme {
                FitFuelApp(appContainer)
            }
        }
    }
}

@Composable
fun FitFuelApp(appContainer: AppContainer) {
    // Create MainViewModel to check if user has completed onboarding
    val mainViewModel: MainViewModel = viewModel {
        MainViewModel(appContainer.userProfileRepository)
    }

    // Observe onboarding status from database
    val shouldShowOnboarding by mainViewModel.shouldShowOnboarding.collectAsState()

    // Show onboarding for first-time users, main app for returning users
    if (shouldShowOnboarding) {
        val onboardingViewModel: OnboardingViewModel = viewModel {
            OnboardingViewModel(appContainer.userProfileRepository)
        }

        OnboardingScreen(
            viewModel = onboardingViewModel,
            onOnboardingComplete = { mainViewModel.markOnboardingCompleted() }
        )
    } else {
        MainScreen(appContainer)
    }
}