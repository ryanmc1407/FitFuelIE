package com.fitfuelie.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitfuelie.app.data.DatabaseProvider
import com.fitfuelie.app.data.model.FitnessGoal
import com.fitfuelie.app.data.model.TrainingFrequency
import com.fitfuelie.app.data.model.DietaryPreference
import com.fitfuelie.app.data.repository.UserProfileRepository

@Composable
fun OnboardingScreen(
    onOnboardingComplete: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: OnboardingViewModel = viewModel {
        val database = DatabaseProvider.getDatabase(context)
        val repository = UserProfileRepository(database.userProfileDao())
        OnboardingViewModel(repository)
    }
    val pagerState = rememberPagerState(pageCount = { 3 })
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }

    // State for selections
    var selectedGoal by remember { mutableStateOf<FitnessGoal?>(null) }
    var selectedTrainingFrequency by remember { mutableStateOf<TrainingFrequency?>(null) }
    var selectedDietaryPreferences by remember { mutableStateOf<Set<DietaryPreference>>(emptySet()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = { (currentPage + 1) / 3f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        )

        // Page content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> GoalsStep()
                1 -> TrainingScheduleStep()
                2 -> DietaryPreferencesStep()
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentPage > 0) {
                OutlinedButton(onClick = {
                    // Handle back navigation
                }) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.width(80.dp))
            }

            Button(
                onClick = {
                    if (currentPage < 2) {
                        // Navigate to next page - for now just show next screen
                        // TODO: Add proper pager navigation
                    } else {
                        // Complete onboarding - save data and navigate
                        viewModel.saveOnboardingData()
                        onOnboardingComplete()
                    }
                }
            ) {
                Text(if (currentPage < 2) "Next" else "Get Started")
            }
        }
    }
}

@Composable
fun GoalsStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "What are your goals?",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Goal selection options
        val goals = listOf(
            FitnessGoal.BUILD_MUSCLE to "Build Muscle",
            FitnessGoal.LOSE_WEIGHT to "Lose Weight",
            FitnessGoal.IMPROVE_PERFORMANCE to "Improve Performance",
            FitnessGoal.MAINTAIN_FITNESS to "Maintain Fitness"
        )

        goals.forEach { (goalEnum, goalText) ->
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                onClick = {
                    selectedGoal = goalEnum
                    viewModel.selectedGoal = goalEnum
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedGoal == goalEnum,
                        onClick = {
                            selectedGoal = goalEnum
                            viewModel.selectedGoal = goalEnum
                        }
                    )
                    Text(
                        text = goalText,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TrainingScheduleStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "How many training days per week?",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Training days selection
        val trainingOptions = listOf(
            TrainingFrequency.DAYS_2_3 to "2-3 days",
            TrainingFrequency.DAYS_4_5 to "4-5 days",
            TrainingFrequency.DAYS_6_PLUS to "6+ days"
        )

        trainingOptions.forEach { (frequencyEnum, frequencyText) ->
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                onClick = {
                    selectedTrainingFrequency = frequencyEnum
                    viewModel.selectedTrainingFrequency = frequencyEnum
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedTrainingFrequency == frequencyEnum,
                        onClick = {
                            selectedTrainingFrequency = frequencyEnum
                            viewModel.selectedTrainingFrequency = frequencyEnum
                        }
                    )
                    Text(
                        text = frequencyText,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DietaryPreferencesStep() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Any dietary preferences?",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Dietary preferences selection
        val preferences = listOf(
            DietaryPreference.VEGETARIAN to "Vegetarian",
            DietaryPreference.VEGAN to "Vegan",
            DietaryPreference.GLUTEN_FREE to "Gluten-Free",
            DietaryPreference.KETO to "Keto",
            DietaryPreference.NO_RESTRICTIONS to "No restrictions"
        )

        preferences.forEach { (preferenceEnum, preferenceText) ->
            OutlinedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                onClick = {
                    selectedDietaryPreferences = if (selectedDietaryPreferences.contains(preferenceEnum)) {
                        selectedDietaryPreferences - preferenceEnum
                    } else {
                        selectedDietaryPreferences + preferenceEnum
                    }
                    viewModel.selectedDietaryPreferences = selectedDietaryPreferences
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = selectedDietaryPreferences.contains(preferenceEnum),
                        onCheckedChange = { checked ->
                            selectedDietaryPreferences = if (checked) {
                                selectedDietaryPreferences + preferenceEnum
                            } else {
                                selectedDietaryPreferences - preferenceEnum
                            }
                            viewModel.selectedDietaryPreferences = selectedDietaryPreferences
                        }
                    )
                    Text(
                        text = preferenceText,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
