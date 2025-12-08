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
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // I need to ask for permission to track physical activity (steps).
    // This launcher handles the result of the permission request.
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->

    }

    // 'LaunchedEffect' runs once when this Composable is first shown.
    LaunchedEffect(Unit) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACTIVITY_RECOGNITION
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(android.Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }
    }

    // This controller manages navigating between screens.
    val navController = rememberNavController()
    
    // These are the screens I want to show in the bottom bar.
    val screens = listOf(
        Screen.Dashboard,
        Screen.MealPlanner,
        Screen.TrainingCalendar,
        Screen.GroceryList,
        Screen.Profile
    )

    // Scaffold is a layout structure that provides slots for common UI elements like a top bar, bottom bar, FAB, etc.
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                screens = screens
            )
        }
    ) { padding ->
        // This is where the main content goes.
        // I pass the padding from the Scaffold so my content doesn't get hidden behind the bottom bar.
        AppNavigation(
            appContainer = appContainer,
            navController = navController,
            modifier = Modifier.padding(padding)
        )
    }
}
