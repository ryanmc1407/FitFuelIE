package com.example.fitfuelie.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fitfuelie.data.local.entity.UserProfile
import com.example.fitfuelie.data.model.DietaryPreference
import com.example.fitfuelie.data.model.Goal
import com.example.fitfuelie.data.model.TrainingFrequency
import com.example.fitfuelie.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (!isEditing && userProfile != null) {
                        IconButton(onClick = { viewModel.startEditing() }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit profile")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            userProfile?.let { profile ->
                if (isEditing) {
                    EditProfileContent(
                        viewModel = viewModel,
                        profile = profile
                    )
                } else {
                    ViewProfileContent(
                        viewModel = viewModel,
                        profile = profile
                    )
                }
            } ?: run {
                // Profile not found
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Profile not found",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            // Loading overlay
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun ViewProfileContent(
    viewModel: ProfileViewModel,
    profile: UserProfile
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Personal Information
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Personal Information",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))

                ProfileField(label = "Name", value = profile.name)
                ProfileField(
                    label = "Fitness Goal",
                    value = profile.goal.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
                )
                ProfileField(
                    label = "Training Frequency",
                    value = profile.trainingFrequency.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
                )
                ProfileField(
                    label = "Dietary Preference",
                    value = profile.dietaryPreference.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }
                )
            }
        }

        // Nutrition Targets
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Daily Nutrition Targets",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    NutritionTargetField(
                        label = "Calories",
                        value = "${profile.dailyCalorieTarget}",
                        modifier = Modifier.weight(1f)
                    )
                    NutritionTargetField(
                        label = "Protein",
                        value = "${profile.dailyProteinTarget}g",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth()) {
                    NutritionTargetField(
                        label = "Carbs",
                        value = "${profile.dailyCarbTarget}g",
                        modifier = Modifier.weight(1f)
                    )
                    NutritionTargetField(
                        label = "Fat",
                        value = "${profile.dailyFatTarget}g",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Actions
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Actions",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = { viewModel.resetOnboarding() },
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
private fun EditProfileContent(
    viewModel: ProfileViewModel,
    profile: UserProfile
) {
    val editedProfile by viewModel.editedProfile.collectAsState()

    editedProfile?.let { currentProfile ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Personal Information
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Personal Information",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    var name by remember { mutableStateOf(currentProfile.name) }
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            viewModel.updateName(it)
                        },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Goal selection
                    Text("Fitness Goal", style = MaterialTheme.typography.bodyMedium)
                    Goal.values().forEach { goal ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.updateGoal(goal) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentProfile.goal == goal,
                                onClick = { viewModel.updateGoal(goal) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(goal.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Training frequency selection
                    Text("Training Frequency", style = MaterialTheme.typography.bodyMedium)
                    TrainingFrequency.values().forEach { frequency ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.updateTrainingFrequency(frequency) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentProfile.trainingFrequency == frequency,
                                onClick = { viewModel.updateTrainingFrequency(frequency) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(frequency.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Dietary preference selection
                    Text("Dietary Preference", style = MaterialTheme.typography.bodyMedium)
                    DietaryPreference.values().forEach { preference ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.updateDietaryPreference(preference) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentProfile.dietaryPreference == preference,
                                onClick = { viewModel.updateDietaryPreference(preference) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(preference.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
            }

            // Nutrition Targets
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Daily Nutrition Targets",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    var calories by remember { mutableStateOf(currentProfile.dailyCalorieTarget.toString()) }
                    var protein by remember { mutableStateOf(currentProfile.dailyProteinTarget.toString()) }
                    var carbs by remember { mutableStateOf(currentProfile.dailyCarbTarget.toString()) }
                    var fat by remember { mutableStateOf(currentProfile.dailyFatTarget.toString()) }

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = calories,
                            onValueChange = {
                                calories = it
                                it.toIntOrNull()?.let { cal ->
                                    viewModel.updateNutritionTargets(cal, protein.toFloatOrNull() ?: 0f, carbs.toFloatOrNull() ?: 0f, fat.toFloatOrNull() ?: 0f)
                                }
                            },
                            label = { Text("Calories") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = protein,
                            onValueChange = {
                                protein = it
                                calories.toIntOrNull()?.let { cal ->
                                    viewModel.updateNutritionTargets(cal, it.toFloatOrNull() ?: 0f, carbs.toFloatOrNull() ?: 0f, fat.toFloatOrNull() ?: 0f)
                                }
                            },
                            label = { Text("Protein (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = carbs,
                            onValueChange = {
                                carbs = it
                                calories.toIntOrNull()?.let { cal ->
                                    viewModel.updateNutritionTargets(cal, protein.toFloatOrNull() ?: 0f, it.toFloatOrNull() ?: 0f, fat.toFloatOrNull() ?: 0f)
                                }
                            },
                            label = { Text("Carbs (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = fat,
                            onValueChange = {
                                fat = it
                                calories.toIntOrNull()?.let { cal ->
                                    viewModel.updateNutritionTargets(cal, protein.toFloatOrNull() ?: 0f, carbs.toFloatOrNull() ?: 0f, it.toFloatOrNull() ?: 0f)
                                }
                            },
                            label = { Text("Fat (g)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.cancelEditing() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = { viewModel.saveProfile() },
                    modifier = Modifier.weight(1f),
                    enabled = viewModel.validateProfile(currentProfile)
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun NutritionTargetField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
