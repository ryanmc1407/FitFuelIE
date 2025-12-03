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

    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appContainer = AppContainer(this)
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
    val mainViewModel: MainViewModel = viewModel {
        MainViewModel(appContainer.userProfileRepository)
    }

    val shouldShowOnboarding by mainViewModel.shouldShowOnboarding.collectAsState()

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