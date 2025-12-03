package com.fitfuelie.app.ui.training

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fitfuelie.app.data.DatabaseProvider
import com.fitfuelie.app.data.model.TrainingSession
import com.fitfuelie.app.data.model.TrainingType
import com.fitfuelie.app.data.model.TrainingIntensity
import com.fitfuelie.app.data.repository.TrainingSessionRepository
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun TrainingCalendarScreen() {
    val context = LocalContext.current
    val viewModel: TrainingViewModel = viewModel {
        val database = DatabaseProvider.getDatabase(context)
        val repository = TrainingSessionRepository(database.trainingSessionDao())
        TrainingViewModel(repository)
    }
    val selectedDate by viewModel.selectedDate.collectAsState()
    val trainingSessions by viewModel.trainingSessionsForSelectedDate.collectAsState(initial = emptyList())
    val sessionStats by viewModel.completedSessionsCount.collectAsState(initial = Pair(0, 0))
    val totalTime by viewModel.totalTrainingTime.collectAsState(initial = 0)

    var showAddSessionDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddSessionDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Training Session")
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
                text = "Training Calendar",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Date selector
            DateSelector(
                selectedDate = selectedDate,
                onDateSelected = { viewModel.selectDate(it) }
            )

            // Training summary card
            TrainingSummaryCard(
                completedCount = sessionStats.first,
                totalCount = sessionStats.second,
                totalTime = totalTime ?: 0
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Training sessions list
            Text(
                text = "Sessions for ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (trainingSessions.isEmpty()) {
                EmptyTrainingView()
            } else {
                TrainingSessionsList(
                    sessions = trainingSessions.sortedBy { it.dateTime },
                    onToggleCompletion = { viewModel.toggleSessionCompletion(it) },
                    onDeleteSession = { viewModel.deleteTrainingSession(it) }
                )
            }
        }

        if (showAddSessionDialog) {
            AddTrainingSessionDialog(
                onDismiss = { showAddSessionDialog = false },
                onSessionAdded = { session ->
                    viewModel.addTrainingSession(session)
                    showAddSessionDialog = false
                },
                selectedDate = selectedDate
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
fun TrainingSummaryCard(
    completedCount: Int,
    totalCount: Int,
    totalTime: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Today's Training",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TrainingStatItem("Completed", "$completedCount/$totalCount")
                TrainingStatItem("Total Time", "${totalTime}min")
                TrainingStatItem("Status", if (completedCount == totalCount && totalCount > 0) "Done" else "In Progress")
            }
        }
    }
}

@Composable
fun TrainingStatItem(label: String, value: String) {
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
fun TrainingSessionsList(
    sessions: List<TrainingSession>,
    onToggleCompletion: (TrainingSession) -> Unit,
    onDeleteSession: (TrainingSession) -> Unit
) {
    LazyColumn {
        items(sessions) { session ->
            TrainingSessionItem(
                session = session,
                onToggleCompletion = { onToggleCompletion(session) },
                onDelete = { onDeleteSession(session) }
            )
        }
    }
}

@Composable
fun TrainingSessionItem(
    session: TrainingSession,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (session.completed)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
            else MaterialTheme.colorScheme.surface
        )
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
                    text = session.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${session.trainingType.name} • ${session.duration} min • ${session.intensity.name}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (session.description != null) {
                    Text(
                        text = session.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onToggleCompletion) {
                    Icon(
                        imageVector = if (session.completed)
                            Icons.Default.CheckCircle
                        else Icons.Default.RadioButtonUnchecked,
                        contentDescription = if (session.completed) "Mark as incomplete" else "Mark as complete",
                        tint = if (session.completed) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete session"
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyTrainingView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "No training sessions scheduled",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Tap the + button to schedule your first training session",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun AddTrainingSessionDialog(
    onDismiss: () -> Unit,
    onSessionAdded: (TrainingSession) -> Unit,
    selectedDate: LocalDate
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TrainingType.STRENGTH) }
    var selectedIntensity by remember { mutableStateOf(TrainingIntensity.MODERATE) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Training Session") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Session Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Training type selector
                Text("Training Type", style = MaterialTheme.typography.bodyMedium)
                TrainingType.entries.forEach { type ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedType == type,
                            onClick = { selectedType = type }
                        )
                        Text(type.name.replace("_", " "), modifier = Modifier.padding(start = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Intensity selector
                Text("Intensity", style = MaterialTheme.typography.bodyMedium)
                TrainingIntensity.entries.forEach { intensity ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedIntensity == intensity,
                            onClick = { selectedIntensity = intensity }
                        )
                        Text(intensity.name, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = duration,
                    onValueChange = { duration = it },
                    label = { Text("Duration (minutes)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val session = TrainingSession(
                        title = title,
                        description = if (description.isBlank()) null else description,
                        trainingType = selectedType,
                        duration = duration.toIntOrNull() ?: 60,
                        intensity = selectedIntensity,
                        dateTime = selectedDate.atTime(9, 0) // Default to 9 AM
                    )
                    onSessionAdded(session)
                },
                enabled = title.isNotBlank() && duration.isNotBlank()
            ) {
                Text("Add Session")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
