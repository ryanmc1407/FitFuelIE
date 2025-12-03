package com.example.fitfuelie.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitfuelie.data.local.entity.Meal
import com.example.fitfuelie.data.local.entity.TrainingSession
import com.example.fitfuelie.data.model.MealType
import com.example.fitfuelie.ui.viewmodel.DashboardViewModel
import com.example.fitfuelie.ui.viewmodel.NutritionSummary
import com.example.fitfuelie.ui.viewmodel.TrainingStats
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onAddMeal: () -> Unit,
    onAddTraining: () -> Unit,
    onViewMeals: () -> Unit,
    onViewTraining: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val todaysNutrition by viewModel.todaysNutrition.collectAsState()
    val todaysTrainingStats by viewModel.todaysTrainingStats.collectAsState()
    val todaysMeals by viewModel.todaysMeals.collectAsState()
    val todaysSessions by viewModel.todaysSessions.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome header
        item {
            Text(
                text = "Welcome back, ${userProfile?.name ?: "Athlete"}!",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        // Today's nutrition summary
        item {
            NutritionSummaryCard(
                nutrition = todaysNutrition,
                targets = userProfile?.let {
                    Triple(it.dailyCalorieTarget, it.dailyProteinTarget, it.dailyFatTarget)
                },
                onAddMeal = onAddMeal,
                onViewMeals = onViewMeals
            )
        }

        // Today's training summary
        item {
            TrainingSummaryCard(
                stats = todaysTrainingStats,
                onAddTraining = onAddTraining,
                onViewTraining = onViewTraining
            )
        }

        // Today's meals
        if (todaysMeals.isNotEmpty()) {
            item {
                Text(
                    text = "Today's Meals",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(todaysMeals.sortedBy { it.type.ordinal }) { meal ->
                MealCard(meal = meal)
            }
        }

        // Today's training sessions
        if (todaysSessions.isNotEmpty()) {
            item {
                Text(
                    text = "Today's Training",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(todaysSessions) { session ->
                TrainingSessionCard(
                    session = session,
                    onToggleComplete = { completed ->
                        viewModel.toggleSessionCompletion(session.id, completed)
                    }
                )
            }
        }
    }
}

@Composable
private fun NutritionSummaryCard(
    nutrition: NutritionSummary,
    targets: Triple<Int, Float, Float>?,
    onAddMeal: () -> Unit,
    onViewMeals: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Nutrition",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onAddMeal) {
                    Icon(Icons.Default.Add, contentDescription = "Add meal")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NutritionItem(
                    label = "Calories",
                    current = nutrition.calories,
                    target = targets?.first,
                    modifier = Modifier.weight(1f)
                )
                NutritionItem(
                    label = "Protein",
                    current = nutrition.protein,
                    target = targets?.second,
                    modifier = Modifier.weight(1f)
                )
                NutritionItem(
                    label = "Fat",
                    current = nutrition.fat,
                    target = targets?.third,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onViewMeals,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Restaurant, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("View All Meals")
            }
        }
    }
}

@Composable
private fun TrainingSummaryCard(
    stats: TrainingStats,
    onAddTraining: () -> Unit,
    onViewTraining: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today's Training",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = onAddTraining) {
                    Icon(Icons.Default.Add, contentDescription = "Add training")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TrainingStatItem(
                    label = "Sessions",
                    value = "${stats.completedSessions}",
                    modifier = Modifier.weight(1f)
                )
                TrainingStatItem(
                    label = "Time",
                    value = "${stats.totalTrainingTime} min",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onViewTraining,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.FitnessCenter, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Training Calendar")
            }
        }
    }
}

@Composable
private fun NutritionItem(
    label: String,
    current: Number,
    target: Number?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = when (current) {
                is Int -> current.toString()
                is Float -> String.format("%.1f", current)
                else -> current.toString()
            },
            style = MaterialTheme.typography.headlineSmall
        )
        target?.let {
            Text(
                text = "/ ${it}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TrainingStatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
private fun MealCard(meal: Meal) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = meal.type.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "${meal.calories} cal",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${meal.protein}g protein",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${meal.carbs}g carbs",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${meal.fat}g fat",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            meal.notes?.let { notes ->
                if (notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun TrainingSessionCard(
    session: TrainingSession,
    onToggleComplete: (Boolean) -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${session.type.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }} • ${session.intensity.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = { onToggleComplete(!session.isCompleted) }) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = if (session.isCompleted) "Mark as incomplete" else "Mark as complete",
                        tint = if (session.isCompleted)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${session.duration} minutes",
                style = MaterialTheme.typography.bodyMedium
            )

            session.notes?.let { notes ->
                if (notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
