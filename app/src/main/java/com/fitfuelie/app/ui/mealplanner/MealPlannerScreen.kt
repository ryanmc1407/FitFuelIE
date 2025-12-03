package com.fitfuelie.app.ui.mealplanner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitfuelie.app.data.DatabaseProvider
import com.fitfuelie.app.data.model.Meal
import com.fitfuelie.app.data.model.MealType
import com.fitfuelie.app.data.repository.MealRepository
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MealPlannerScreen() {
    val context = LocalContext.current
    val viewModel: MealPlannerViewModel = viewModel {
        val database = DatabaseProvider.getDatabase(context)
        val repository = MealRepository(database.mealDao())
        MealPlannerViewModel(repository)
    }
    val selectedDate by viewModel.selectedDate.collectAsState()
    val mealsForDate by viewModel.mealsForSelectedDate.collectAsState(initial = emptyList())
    val nutritionSummary by viewModel.dailyNutritionSummary.collectAsState(
        initial = DailyNutritionSummary(0, 0.0, 0.0, 0.0)
    )

    var showAddMealDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddMealDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Meal")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Meal Planner",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Date selector (simplified - can be enhanced with a date picker)
            DateSelector(
                selectedDate = selectedDate,
                onDateSelected = { viewModel.selectDate(it) }
            )

            // Nutrition summary card
            NutritionSummaryCard(nutritionSummary)

            Spacer(modifier = Modifier.height(16.dp))

            // Meals list
            Text(
                text = "Meals for ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (mealsForDate.isEmpty()) {
                EmptyMealsView()
            } else {
                MealsList(
                    meals = mealsForDate,
                    onMealClick = { /* TODO: Show meal details */ },
                    onDeleteMeal = { viewModel.deleteMeal(it) }
                )
            }
        }

        if (showAddMealDialog) {
            AddMealDialog(
                onDismiss = { showAddMealDialog = false },
                onMealAdded = { meal ->
                    viewModel.addMeal(meal)
                    showAddMealDialog = false
                }
            )
        }
    }
}

@Composable
fun DateSelector(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val dates = (-2..2).map { selectedDate.plusDays(it.toLong()) }

        dates.forEach { date ->
            val isSelected = date == selectedDate
            OutlinedButton(
                onClick = { onDateSelected(date) },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = date.format(DateTimeFormatter.ofPattern("EEE\ndd")),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun NutritionSummaryCard(summary: DailyNutritionSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Daily Nutrition",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NutritionItem("Calories", "${summary.calories}")
                NutritionItem("Protein", "${summary.protein}g")
                NutritionItem("Carbs", "${summary.carbs}g")
                NutritionItem("Fat", "${summary.fat}g")
            }
        }
    }
}

@Composable
fun NutritionItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun MealsList(
    meals: List<Meal>,
    onMealClick: (Meal) -> Unit,
    onDeleteMeal: (Meal) -> Unit
) {
    LazyColumn {
        items(meals.sortedBy { it.dateTime }) { meal ->
            MealItem(
                meal = meal,
                onClick = { onMealClick(meal) },
                onDelete = { onDeleteMeal(meal) }
            )
        }
    }
}

@Composable
fun MealItem(
    meal: Meal,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${meal.mealType.name} • ${meal.calories} cal",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "P: ${meal.protein}g • C: ${meal.carbs}g • F: ${meal.fat}g",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete meal"
                )
            }
        }
    }
}

@Composable
fun EmptyMealsView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No meals logged for this day",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Tap the + button to add your first meal",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun AddMealDialog(
    onDismiss: () -> Unit,
    onMealAdded: (Meal) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var carbs by remember { mutableStateOf("") }
    var fat by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf(MealType.BREAKFAST) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Meal") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Meal Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Meal type selector
                Text("Meal Type", style = MaterialTheme.typography.bodyMedium)
                MealType.entries.forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMealType == type,
                            onClick = { selectedMealType = type }
                        )
                        Text(type.name, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { calories = it },
                        label = { Text("Calories") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { protein = it },
                        label = { Text("Protein (g)") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { carbs = it },
                        label = { Text("Carbs (g)") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = fat,
                        onValueChange = { fat = it },
                        label = { Text("Fat (g)") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val meal = Meal(
                        name = name,
                        mealType = selectedMealType,
                        calories = calories.toIntOrNull() ?: 0,
                        protein = protein.toDoubleOrNull() ?: 0.0,
                        carbs = carbs.toDoubleOrNull() ?: 0.0,
                        fat = fat.toDoubleOrNull() ?: 0.0
                    )
                    onMealAdded(meal)
                },
                enabled = name.isNotBlank() && calories.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
