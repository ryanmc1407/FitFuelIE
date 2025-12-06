package com.example.fitfuelie.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitfuelie.data.local.entity.Meal
import com.example.fitfuelie.data.model.MealType
import com.example.fitfuelie.ui.viewmodel.MealPlannerViewModel
import com.example.fitfuelie.ui.viewmodel.NutritionSummary
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerScreen(
    viewModel: MealPlannerViewModel,
    onNavigateBack: () -> Unit
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val meals by viewModel.mealsForSelectedDate.collectAsState()
    val dailyNutrition by viewModel.dailyNutritionSummary.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showAddMealDialog by remember { mutableStateOf(false) }
    var editingMeal by remember { mutableStateOf<Meal?>(null) }

    val dateFormatter = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Planner") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddMealDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add meal")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Date picker
            DatePickerCard(
                selectedDate = selectedDate,
                onDateSelected = { viewModel.selectDate(it) }
            )

            // Daily nutrition summary
            NutritionSummaryCard(nutrition = dailyNutrition)

            // Meals list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (meals.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No meals added for this date.\nTap the + button to add your first meal!",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(meals.sortedBy { it.type.ordinal }) { meal ->
                        MealCard(
                            meal = meal,
                            onEdit = { editingMeal = meal }
                        )
                    }
                }
            }
        }
    }

    // Add meal dialog
    if (showAddMealDialog) {
        AddEditMealDialog(
            meal = null,
            onDismiss = { showAddMealDialog = false },
            onSave = { name, type, calories, protein, carbs, fat, notes, addToGrocery ->
                viewModel.addMeal(name, type, calories, protein, carbs, fat, notes, addToGrocery)
                showAddMealDialog = false
            }
        )
    }

    // Edit meal dialog
    editingMeal?.let { meal ->
        AddEditMealDialog(
            meal = meal,
            onDismiss = { editingMeal = null },
            onSave = { name, type, calories, protein, carbs, fat, notes, _ ->
                val updatedMeal = meal.copy(
                    name = name,
                    type = type,
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat,
                    notes = notes
                )
                viewModel.updateMeal(updatedMeal)
                editingMeal = null
            }
        )
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

@Composable
private fun DatePickerCard(
    selectedDate: Date,
    onDateSelected: (Date) -> Unit
) {
    val dateFormatter = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Selected Date",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = dateFormatter.format(selectedDate),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Simple date navigation (can be enhanced with a proper date picker)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        calendar.time = selectedDate
                        calendar.add(Calendar.DAY_OF_MONTH, -1)
                        onDateSelected(calendar.time)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Previous")
                }

                OutlinedButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        calendar.time = selectedDate
                        calendar.add(Calendar.DAY_OF_MONTH, 1)
                        onDateSelected(calendar.time)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Next")
                }
            }
        }
    }
}

@Composable
private fun NutritionSummaryCard(nutrition: NutritionSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Daily Totals",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NutritionItem(
                    label = "Calories",
                    value = "${nutrition.calories}",
                    modifier = Modifier.weight(1f)
                )
                NutritionItem(
                    label = "Protein",
                    value = "${nutrition.protein}g",
                    modifier = Modifier.weight(1f)
                )
                NutritionItem(
                    label = "Carbs",
                    value = "${nutrition.carbs}g",
                    modifier = Modifier.weight(1f)
                )
                NutritionItem(
                    label = "Fat",
                    value = "${nutrition.fat}g",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NutritionItem(
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
private fun MealCard(
    meal: Meal,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
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

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit meal")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
private fun AddEditMealDialog(
    meal: Meal?,
    onDismiss: () -> Unit,
    onSave: (String, MealType, Int, Float, Float, Float, String?, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(meal?.name ?: "") }
    var selectedType by remember { mutableStateOf(meal?.type ?: MealType.BREAKFAST) }
    var calories by remember { mutableStateOf(meal?.calories?.toString() ?: "") }
    var protein by remember { mutableStateOf(meal?.protein?.toString() ?: "") }
    var carbs by remember { mutableStateOf(meal?.carbs?.toString() ?: "") }
    var fat by remember { mutableStateOf(meal?.fat?.toString() ?: "") }
    var notes by remember { mutableStateOf(meal?.notes ?: "") }
    var addToGroceryList by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (meal == null) "Add Meal" else "Edit Meal") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Meal Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Meal type selection
                Text("Meal Type", style = MaterialTheme.typography.bodyMedium)
                MealType.values().forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedType = type },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == type,
                            onClick = { selectedType = type }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(type.name.lowercase().replaceFirstChar { it.uppercase() })
                    }
                }

                // Nutrition inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = calories,
                        onValueChange = { calories = it },
                        label = { Text("Calories") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = protein,
                        onValueChange = { protein = it },
                        label = { Text("Protein (g)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = carbs,
                        onValueChange = { carbs = it },
                        label = { Text("Carbs (g)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    OutlinedTextField(
                        value = fat,
                        onValueChange = { fat = it },
                        label = { Text("Fat (g)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                // Add to grocery list checkbox (only for new meals)
                if (meal == null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { addToGroceryList = !addToGroceryList },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = addToGroceryList,
                            onCheckedChange = { addToGroceryList = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add ingredients to grocery list?")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cal = calories.toIntOrNull() ?: 0
                    val prot = protein.toFloatOrNull() ?: 0f
                    val carb = carbs.toFloatOrNull() ?: 0f
                    val ft = fat.toFloatOrNull() ?: 0f
                    val finalNotes = notes.takeIf { it.isNotBlank() }

                    onSave(name, selectedType, cal, prot, carb, ft, finalNotes, addToGroceryList)
                },
                enabled = name.isNotBlank()
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
