package com.example.fitfuelie.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitfuelie.ui.viewmodel.OnboardingViewModel

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onOnboardingComplete: () -> Unit
) {
    val currentStep by viewModel.currentStep.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

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
        // Progress indicator
        LinearProgressIndicator(
            progress = (currentStep + 1) / 3f,
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
                    IconButton(onClick = { viewModel.clearError() }) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss error")
                    }
                }
            }
        }

        // Step content
        when (currentStep) {
            0 -> GoalSelectionStep(viewModel)
            1 -> TrainingFrequencyStep(viewModel)
            2 -> DietaryPreferenceStep(viewModel)
        }

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 0) {
                OutlinedButton(onClick = { viewModel.previousStep() }) {
                    Text("Previous")
                }
            } else {
                Spacer(modifier = Modifier.width(80.dp))
            }

            if (currentStep < 2) {
                Button(
                    onClick = { viewModel.nextStep() },
                    enabled = viewModel.canProceedToNextStep()
                ) {
                    Text("Next")
                }
            } else {
                Button(
                    onClick = {
                        viewModel.completeOnboarding()
                        onOnboardingComplete()
                    },
                    enabled = viewModel.canProceedToNextStep()
                ) {
                    Text("Get Started")
                }
            }
        }
    }
}

@Composable
private fun GoalSelectionStep(viewModel: OnboardingViewModel) {
    val selectedGoal by viewModel.selectedGoal.collectAsState()

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
            com.example.fitfuelie.data.model.Goal.BUILD_MUSCLE to "Build Muscle",
            com.example.fitfuelie.data.model.Goal.LOSE_WEIGHT to "Lose Weight",
            com.example.fitfuelie.data.model.Goal.IMPROVE_PERFORMANCE to "Improve Performance",
            com.example.fitfuelie.data.model.Goal.MAINTAIN_FITNESS to "Maintain Fitness"
        )

        goals.forEach { (goal, displayName) ->
            GoalCard(
                title = displayName,
                isSelected = selectedGoal == goal,
                onClick = { viewModel.selectGoal(goal) }
            )
        }
    }
}

@Composable
private fun TrainingFrequencyStep(viewModel: OnboardingViewModel) {
    val selectedFrequency by viewModel.selectedTrainingFrequency.collectAsState()

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
            com.example.fitfuelie.data.model.TrainingFrequency.TWO_THREE_DAYS to "2-3 days per week",
            com.example.fitfuelie.data.model.TrainingFrequency.FOUR_FIVE_DAYS to "4-5 days per week",
            com.example.fitfuelie.data.model.TrainingFrequency.SIX_PLUS_DAYS to "6+ days per week"
        )

        frequencies.forEach { (frequency, displayName) ->
            FrequencyCard(
                title = displayName,
                isSelected = selectedFrequency == frequency,
                onClick = { viewModel.selectTrainingFrequency(frequency) }
            )
        }
    }
}

@Composable
private fun DietaryPreferenceStep(viewModel: OnboardingViewModel) {
    val selectedPreference by viewModel.selectedDietaryPreference.collectAsState()
    val userName by viewModel.userName.collectAsState()

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
            onValueChange = { viewModel.updateUserName(it) },
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
            com.example.fitfuelie.data.model.DietaryPreference.VEGETARIAN to "Vegetarian",
            com.example.fitfuelie.data.model.DietaryPreference.VEGAN to "Vegan",
            com.example.fitfuelie.data.model.DietaryPreference.GLUTEN_FREE to "Gluten-Free",
            com.example.fitfuelie.data.model.DietaryPreference.KETO to "Keto",
            com.example.fitfuelie.data.model.DietaryPreference.NO_RESTRICTIONS to "No restrictions"
        )

        preferences.forEach { (preference, displayName) ->
            PreferenceCard(
                title = displayName,
                isSelected = selectedPreference == preference,
                onClick = { viewModel.selectDietaryPreference(preference) }
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
