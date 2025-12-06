package com.example.fitfuelie.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitfuelie.data.model.DietaryPreference
import com.example.fitfuelie.data.model.Goal
import com.example.fitfuelie.data.model.TrainingFrequency
import com.example.fitfuelie.ui.theme.FitFuelIETheme
import com.example.fitfuelie.ui.viewmodel.OnboardingViewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingComplete: () -> Unit
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val selectedGoal by viewModel.selectedGoal.collectAsState()
    val selectedFrequency by viewModel.selectedTrainingFrequency.collectAsState()
    val selectedPreference by viewModel.selectedDietaryPreference.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val weight by viewModel.weight.collectAsState()
    val isNextEnabled by viewModel.isNextEnabled.collectAsState()

    OnboardingScreenContent(
        currentStep = currentStep,
        isLoading = isLoading,
        error = error,
        selectedGoal = selectedGoal,
        selectedFrequency = selectedFrequency,
        selectedPreference = selectedPreference,
        userName = userName,
        weight = weight,
        isNextEnabled = isNextEnabled,
        onNext = viewModel::nextStep,
        onPrevious = viewModel::previousStep,
        onComplete = {
            viewModel.completeOnboarding()
            onOnboardingComplete()
        },
        onClearError = viewModel::clearError,
        onSelectGoal = viewModel::selectGoal,
        onSelectFrequency = viewModel::selectTrainingFrequency,
        onSelectPreference = viewModel::selectDietaryPreference,
        onUpdateName = viewModel::updateUserName,
        onUpdateWeight = viewModel::updateWeight
    )
}

@Composable
fun OnboardingScreenContent(
    currentStep: Int,
    isLoading: Boolean,
    error: String?,
    selectedGoal: Goal?,
    selectedFrequency: TrainingFrequency?,
    selectedPreference: DietaryPreference?,
    userName: String,
    weight: String,
    isNextEnabled: Boolean,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onComplete: () -> Unit,
    onClearError: () -> Unit,
    onSelectGoal: (Goal) -> Unit,
    onSelectFrequency: (TrainingFrequency) -> Unit,
    onSelectPreference: (DietaryPreference) -> Unit,
    onUpdateName: (String) -> Unit,
    onUpdateWeight: (String) -> Unit
) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Progress indicator (4 steps now)
        LinearProgressIndicator(
            progress = (currentStep + 1) / 4f,
            modifier = Modifier.fillMaxWidth()
        )

        // Error display
        error?.let {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onClearError) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss error")
                    }
                }
            }
        }

        // Step content
        when (currentStep) {
            0 -> GoalSelectionStep(
                selectedGoal = selectedGoal,
                onSelectGoal = onSelectGoal
            )
            1 -> TrainingFrequencyStep(
                selectedFrequency = selectedFrequency,
                onSelectFrequency = onSelectFrequency
            )
            2 -> WeightInputStep(
                weight = weight,
                onUpdateWeight = onUpdateWeight
            )
            3 -> DietaryPreferenceStep(
                selectedPreference = selectedPreference,
                userName = userName,
                onSelectPreference = onSelectPreference,
                onUpdateName = onUpdateName
            )
        }

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 0) {
                OutlinedButton(onClick = onPrevious) {
                    Text("Previous")
                }
            } else {
                Spacer(modifier = Modifier.width(80.dp))
            }

            if (currentStep < 3) {
                Button(
                    onClick = onNext,
                    enabled = isNextEnabled
                ) {
                    Text("Next")
                }
            } else {
                Button(
                    onClick = onComplete,
                    enabled = isNextEnabled
                ) {
                    Text("Get Started")
                }
            }
        }
    }
}

@Composable
private fun GoalSelectionStep(
    selectedGoal: Goal?,
    onSelectGoal: (Goal) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "What's your fitness goal?",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "This helps us personalize your nutrition recommendations",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        val goals = listOf(
            Goal.BUILD_MUSCLE to "Build Muscle",
            Goal.LOSE_WEIGHT to "Lose Weight",
            Goal.IMPROVE_PERFORMANCE to "Improve Performance",
            Goal.MAINTAIN_FITNESS to "Maintain Fitness"
        )

        goals.forEach { (goal, displayName) ->
            GoalCard(
                title = displayName,
                isSelected = selectedGoal == goal,
                onClick = { onSelectGoal(goal) }
            )
        }
    }
}

@Composable
private fun TrainingFrequencyStep(
    selectedFrequency: TrainingFrequency?,
    onSelectFrequency: (TrainingFrequency) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "How often do you train?",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "This helps us calculate your nutrition needs",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        val frequencies = listOf(
            TrainingFrequency.TWO_THREE_DAYS to "2-3 days per week",
            TrainingFrequency.FOUR_FIVE_DAYS to "4-5 days per week",
            TrainingFrequency.SIX_PLUS_DAYS to "6+ days per week"
        )

        frequencies.forEach { (frequency, displayName) ->
            FrequencyCard(
                title = displayName,
                isSelected = selectedFrequency == frequency,
                onClick = { onSelectFrequency(frequency) }
            )
        }
    }
}

@Composable
private fun DietaryPreferenceStep(
    selectedPreference: DietaryPreference?,
    userName: String,
    onSelectPreference: (DietaryPreference) -> Unit,
    onUpdateName: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "What's your name?",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = userName,
            onValueChange = onUpdateName,
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Any dietary preferences?",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Text(
            text = "We'll tailor meal suggestions to your preferences",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        val preferences = listOf(
            DietaryPreference.VEGETARIAN to "Vegetarian",
            DietaryPreference.VEGAN to "Vegan",
            DietaryPreference.GLUTEN_FREE to "Gluten-Free",
            DietaryPreference.KETO to "Keto",
            DietaryPreference.NO_RESTRICTIONS to "No restrictions"
        )

        preferences.forEach { (preference, displayName) ->
            PreferenceCard(
                title = displayName,
                isSelected = selectedPreference == preference,
                onClick = { onSelectPreference(preference) }
            )
        }
    }
}

@Composable
private fun GoalCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun FrequencyCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun WeightInputStep(
    weight: String,
    onUpdateWeight: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "What's your weight?",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "This helps us calculate your daily calorie needs",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)

        OutlinedTextField(
            value = weight,
            onValueChange = onUpdateWeight,
            label = { Text("Weight (kg)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
            )
        )
    }
}

@Composable
private fun PreferenceCard(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    FitFuelIETheme {
        OnboardingScreenContent(
            currentStep = 0,
            isLoading = false,
            error = null,
            selectedGoal = null,
            selectedFrequency = null,
            selectedPreference = null,
            userName = "",
            weight = "",
            isNextEnabled = false,
            onNext = {},
            onPrevious = {},
            onComplete = {},
            onClearError = {},
            onSelectGoal = {},
            onSelectFrequency = {},
            onSelectPreference = {},
            onUpdateName = {},
            onUpdateWeight = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenStep1Preview() {
    FitFuelIETheme {
        OnboardingScreenContent(
            currentStep = 0,
            isLoading = false,
            error = null,
            selectedGoal = Goal.BUILD_MUSCLE,
            selectedFrequency = null,
            selectedPreference = null,
            userName = "",
            weight = "",
            isNextEnabled = true,
            onNext = {},
            onPrevious = {},
            onComplete = {},
            onClearError = {},
            onSelectGoal = {},
            onSelectFrequency = {},
            onSelectPreference = {},
            onUpdateName = {},
            onUpdateWeight = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenStep2Preview() {
    FitFuelIETheme {
        OnboardingScreenContent(
            currentStep = 1,
            isLoading = false,
            error = null,
            selectedGoal = Goal.BUILD_MUSCLE,
            selectedFrequency = null,
            selectedPreference = null,
            userName = "",
            weight = "",
            isNextEnabled = false,
            onNext = {},
            onPrevious = {},
            onComplete = {},
            onClearError = {},
            onSelectGoal = {},
            onSelectFrequency = {},
            onSelectPreference = {},
            onUpdateName = {},
            onUpdateWeight = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenStep3Preview() {
    FitFuelIETheme {
        OnboardingScreenContent(
            currentStep = 2,
            isLoading = false,
            error = null,
            selectedGoal = Goal.BUILD_MUSCLE,
            selectedFrequency = TrainingFrequency.FOUR_FIVE_DAYS,
            selectedPreference = null,
            userName = "",
            weight = "75",
            isNextEnabled = true,
            onNext = {},
            onPrevious = {},
            onComplete = {},
            onClearError = {},
            onSelectGoal = {},
            onSelectFrequency = {},
            onSelectPreference = {},
            onUpdateName = {},
            onUpdateWeight = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenStep4Preview() {
    FitFuelIETheme {
        OnboardingScreenContent(
            currentStep = 3,
            isLoading = false,
            error = null,
            selectedGoal = Goal.BUILD_MUSCLE,
            selectedFrequency = TrainingFrequency.FOUR_FIVE_DAYS,
            selectedPreference = null,
            userName = "Ryan",
            weight = "75",
            isNextEnabled = false,
            onNext = {},
            onPrevious = {},
            onComplete = {},
            onClearError = {},
            onSelectGoal = {},
            onSelectFrequency = {},
            onSelectPreference = {},
            onUpdateName = {},
            onUpdateWeight = {}
        )
    }
}
