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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fitfuelie.data.local.entity.TrainingSession
import com.example.fitfuelie.data.model.Intensity
import com.example.fitfuelie.data.model.TrainingType
import com.example.fitfuelie.ui.viewmodel.TrainingCalendarViewModel
import java.text.SimpleDateFormat
import java.util.*

// This screen handles the training calendar. I used a ViewModel to manage the state
// so that the UI updates automatically when data changes.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingCalendarScreen(
    viewModel: TrainingCalendarViewModel,
    onNavigateBack: () -> Unit
) {
    // Collecting the state from the ViewModel. Using collectAsState so Compose knows when to redraw.
    val selectedDate by viewModel.selectedDate.collectAsState()
    val sessions by viewModel.sessionsForSelectedDate.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val message by viewModel.message.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    // I used LaunchedEffect here because I only want to show the Toast when the error changes.
    // If I didn't use this, the Toast might show up every time the screen recomposes!
    LaunchedEffect(error) {
        error?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    // Same thing here for success messages (like when a workout is generated)
    LaunchedEffect(message) {
        message?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    var showAddSessionDialog by remember { mutableStateOf(false) }
    var editingSession by remember { mutableStateOf<TrainingSession?>(null) }

    val dateFormatter = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Training Calendar") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.generateDailyWorkout() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Generate daily workout")
                    }
                    IconButton(onClick = { showAddSessionDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add session")
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

            // Sessions list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (sessions.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "No training sessions for this date.",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(onClick = { viewModel.generateDailyWorkout() }) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Generate Daily Workout")
                            }
                            Text(
                                text = "or tap + to add manually",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(sessions) { session ->
                        TrainingSessionCard(
                            session = session,
                            onEdit = { editingSession = session },
                            onToggleComplete = { completed ->
                                viewModel.toggleSessionCompletion(session.id, completed)
                            }
                        )
                    }
                }
            }
        }
    }

            // Add session dialog
    if (showAddSessionDialog) {
        AddEditTrainingSessionDialog(
            session = null,
            onDismiss = { showAddSessionDialog = false },
            onSave = { title, type, intensity, duration, notes ->
                viewModel.addTrainingSession(title, type, intensity, duration, notes)
                showAddSessionDialog = false
                android.widget.Toast.makeText(context, "Training session added!", android.widget.Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Edit session dialog
    editingSession?.let { session ->
        AddEditTrainingSessionDialog(
            session = session,
            onDismiss = { editingSession = null },
            onSave = { title, type, intensity, duration, notes ->
                val updatedSession = session.copy(
                    title = title,
                    type = type,
                    intensity = intensity,
                    duration = duration,
                    notes = notes
                )
                viewModel.updateTrainingSession(updatedSession)
                editingSession = null
                android.widget.Toast.makeText(context, "Training session updated!", android.widget.Toast.LENGTH_SHORT).show()
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

            // Simple date navigation
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
private fun TrainingSessionCard(
    session: TrainingSession,
    onEdit: () -> Unit,
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

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit session")
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
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${session.duration} minutes",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = if (session.isCompleted) "Completed" else "Pending",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (session.isCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

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

// This dialog is used for both adding AND editing sessions.
// If 'session' is null, we know we are adding a new one. If it's not null, we are editing.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddEditTrainingSessionDialog(
    session: TrainingSession?,
    onDismiss: () -> Unit,
    onSave: (String, TrainingType, Intensity, Int, String?) -> Unit
) {
    var title by remember { mutableStateOf(session?.title ?: "") }
    var selectedType by remember { mutableStateOf(session?.type ?: TrainingType.STRENGTH) }
    var selectedIntensity by remember { mutableStateOf(session?.intensity ?: Intensity.MODERATE) }
    var duration by remember { mutableStateOf(session?.duration?.toString() ?: "") }
    var notes by remember { mutableStateOf(session?.notes ?: "") }
    
    var expandedType by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (session == null) "Add Training Session" else "Edit Training Session") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Session Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Training type selection (Dropdown)
                ExposedDropdownMenuBox(
                    expanded = expandedType,
                    onExpandedChange = { expandedType = !expandedType }
                ) {
                    OutlinedTextField(
                        value = selectedType.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Training Type") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedType,
                        onDismissRequest = { expandedType = false }
                    ) {
                        TrainingType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    selectedType = type
                                    expandedType = false
                                }
                            )
                        }
                    }
                }

                // Intensity selection (Row of Chips)
                Column {
                    Text("Intensity", style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Intensity.values().forEach { intensity ->
                            FilterChip(
                                selected = selectedIntensity == intensity,
                                onClick = { selectedIntensity = intensity },
                                label = { Text(intensity.name.lowercase().replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (min)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val dur = duration.trim().toIntOrNull() ?: 0
                    val finalNotes = notes.takeIf { it.isNotBlank() }

                    onSave(title.trim(), selectedType, selectedIntensity, dur, finalNotes)
                },
                enabled = title.isNotBlank() && duration.trim().toIntOrNull() != null
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
