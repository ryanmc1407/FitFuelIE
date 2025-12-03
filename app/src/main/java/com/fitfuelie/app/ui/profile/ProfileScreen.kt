package com.fitfuelie.app.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitfuelie.app.data.DatabaseProvider
import com.fitfuelie.app.data.model.*
import com.fitfuelie.app.data.repository.UserProfileRepository

@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = viewModel {
        val database = DatabaseProvider.getDatabase(context)
        val repository = UserProfileRepository(database.userProfileDao())
        ProfileViewModel(repository)
    }
    val userProfile by viewModel.userProfile.collectAsState(initial = null)
    var showEditDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (userProfile == null) {
                // Loading or empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                ProfileContent(userProfile!!)
            }
        }

        if (showEditDialog && userProfile != null) {
            EditProfileDialog(
                currentProfile = userProfile!!,
                onDismiss = { showEditDialog = false },
                onProfileUpdated = { updatedProfile ->
                    viewModel.updateProfile(
                        name = updatedProfile.name,
                        goal = updatedProfile.goal,
                        trainingFrequency = updatedProfile.trainingFrequency,
                        dietaryPreferences = updatedProfile.dietaryPreferences,
                        dailyCalorieGoal = updatedProfile.dailyCalorieGoal,
                        dailyProteinGoal = updatedProfile.dailyProteinGoal,
                        dailyCarbGoal = updatedProfile.dailyCarbGoal,
                        dailyFatGoal = updatedProfile.dailyFatGoal
                    )
                    showEditDialog = false
                }
            )
        }
    }
}

@Composable
fun ProfileContent(profile: UserProfile) {
    Column {
        // Personal Information
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ProfileField("Name", profile.name ?: "Not set")
            }
        }

        // Fitness Goals
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Fitness Goals",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ProfileField("Goal", profile.goal?.name?.replace("_", " ") ?: "Not set")
                ProfileField("Training Frequency", profile.trainingFrequency?.name?.replace("_", " ") ?: "Not set")
            }
        }

        // Dietary Preferences
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Dietary Preferences",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (profile.dietaryPreferences.isEmpty()) {
                    Text(
                        text = "No dietary preferences set",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    profile.dietaryPreferences.forEach { preference ->
                        Text(
                            text = "• ${preference.name.replace("_", " ")}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Nutrition Goals
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Daily Nutrition Goals",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ProfileField("Calories", profile.dailyCalorieGoal?.toString() ?: "Not set")
                ProfileField("Protein", profile.dailyProteinGoal?.let { "${it}g" } ?: "Not set")
                ProfileField("Carbohydrates", profile.dailyCarbGoal?.let { "${it}g" } ?: "Not set")
                ProfileField("Fat", profile.dailyFatGoal?.let { "${it}g" } ?: "Not set")
            }
        }

        // Actions
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Actions",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedButton(
                    onClick = { /* TODO: Implement data export */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Export Data")
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { /* TODO: Implement onboarding reset */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reset Onboarding")
                }
            }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun EditProfileDialog(
    currentProfile: UserProfile,
    onDismiss: () -> Unit,
    onProfileUpdated: (UserProfile) -> Unit
) {
    var name by remember { mutableStateOf(currentProfile.name ?: "") }
    var selectedGoal by remember { mutableStateOf(currentProfile.goal) }
    var selectedTrainingFrequency by remember { mutableStateOf(currentProfile.trainingFrequency) }
    var selectedDietaryPreferences by remember { mutableStateOf(currentProfile.dietaryPreferences) }
    var dailyCalorieGoal by remember { mutableStateOf(currentProfile.dailyCalorieGoal?.toString() ?: "") }
    var dailyProteinGoal by remember { mutableStateOf(currentProfile.dailyProteinGoal?.toString() ?: "") }
    var dailyCarbGoal by remember { mutableStateOf(currentProfile.dailyCarbGoal?.toString() ?: "") }
    var dailyFatGoal by remember { mutableStateOf(currentProfile.dailyFatGoal?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .heightIn(max = 400.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Fitness Goal
                Text("Fitness Goal", style = MaterialTheme.typography.bodyMedium)
                FitnessGoal.entries.forEach { goal ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedGoal == goal,
                            onClick = { selectedGoal = goal }
                        )
                        Text(goal.name.replace("_", " "), modifier = Modifier.padding(start = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Training Frequency
                Text("Training Frequency", style = MaterialTheme.typography.bodyMedium)
                TrainingFrequency.entries.forEach { frequency ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedTrainingFrequency == frequency,
                            onClick = { selectedTrainingFrequency = frequency }
                        )
                        Text(frequency.name.replace("_", " "), modifier = Modifier.padding(start = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dietary Preferences
                Text("Dietary Preferences", style = MaterialTheme.typography.bodyMedium)
                DietaryPreference.entries.forEach { preference ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedDietaryPreferences.contains(preference),
                            onCheckedChange = { checked ->
                                selectedDietaryPreferences = if (checked) {
                                    selectedDietaryPreferences + preference
                                } else {
                                    selectedDietaryPreferences - preference
                                }
                            }
                        )
                        Text(preference.name.replace("_", " "), modifier = Modifier.padding(start = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nutrition Goals
                Text("Daily Nutrition Goals", style = MaterialTheme.typography.bodyMedium)

                OutlinedTextField(
                    value = dailyCalorieGoal,
                    onValueChange = { dailyCalorieGoal = it },
                    label = { Text("Daily Calories") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = dailyProteinGoal,
                        onValueChange = { dailyProteinGoal = it },
                        label = { Text("Protein (g)") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = dailyCarbGoal,
                        onValueChange = { dailyCarbGoal = it },
                        label = { Text("Carbs (g)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dailyFatGoal,
                    onValueChange = { dailyFatGoal = it },
                    label = { Text("Fat (g)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedProfile = currentProfile.copy(
                        name = if (name.isBlank()) null else name,
                        goal = selectedGoal,
                        trainingFrequency = selectedTrainingFrequency,
                        dietaryPreferences = selectedDietaryPreferences,
                        dailyCalorieGoal = dailyCalorieGoal.toIntOrNull(),
                        dailyProteinGoal = dailyProteinGoal.toDoubleOrNull(),
                        dailyCarbGoal = dailyCarbGoal.toDoubleOrNull(),
                        dailyFatGoal = dailyFatGoal.toDoubleOrNull()
                    )
                    onProfileUpdated(updatedProfile)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
