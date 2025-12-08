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


class MainActivity : ComponentActivity() {

    /**
     * App container for dependency injection
     * 
     * I use 'lateinit' because:
     * - I can't create it in the constructor (I need the Context)
     * - I promise to initialize it in onCreate() before using it
     * - The compiler trusts me, but will crash if I forget!
     */
    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Here I initialize the container so I can use it later.
        appContainer = AppContainer(this)
        
        // This makes the app go all the way to the edge of the screen, behind the status bar.
        // It looks more modern this way.
        enableEdgeToEdge()

        // This is where I start drawing the UI with Jetpack Compose.
        setContent {
            FitFuelIETheme {
                // I pass the container down so other parts of the app can use it.
                FitFuelApp(appContainer)
            }
        }
    }
}

@Composable
fun FitFuelApp(appContainer: AppContainer) {
    // I need the MainViewModel to check if the user has finished the onboarding steps.
    // 'viewModel' is a helper function to get the ViewModel.
    val mainViewModel: MainViewModel = viewModel {
        MainViewModel(appContainer.userProfileRepository)
    }

    // I use 'collectAsState' to watch the 'shouldShowOnboarding' value.
    // If it changes, the UI will automatically update!
    val shouldShowOnboarding by mainViewModel.shouldShowOnboarding.collectAsState()

    // If the user is new, I show them the onboarding screen.
    // If they've been here before, I show the main screen.
    if (shouldShowOnboarding) {
        val onboardingViewModel: OnboardingViewModel = viewModel {
            OnboardingViewModel(
                appContainer.userProfileRepository,
                appContainer.mealRepository
            )
        }

        OnboardingScreen(
            viewModel = onboardingViewModel,
            onOnboardingComplete = { 
                // When they finish onboarding, I tell the ViewModel so it can save that info.
                mainViewModel.markOnboardingCompleted() 
            }
        )
    } else {
        MainScreen(appContainer)
    }
}