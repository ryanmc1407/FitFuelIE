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
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Permission result handled automatically by system/sensor manager
    }

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
            navController = navController,
            modifier = Modifier.padding(padding)
        )
    }
}
