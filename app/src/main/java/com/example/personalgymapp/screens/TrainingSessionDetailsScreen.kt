package com.example.personalgymapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.R
import com.example.personalgymapp.model.TrainingSession
import com.example.personalgymapp.model.WorkoutPlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingSessionDetailsScreen(
    trainingSession: TrainingSession?,
    workoutPlan: WorkoutPlan?,
    availableWorkoutPlans: List<WorkoutPlan>,
    onWorkoutPlanClick: (Int) -> Unit,
    onViewSessionResultsClick: (Int) -> Unit,
    onStartWorkoutClick: (Int) -> Unit,
    onUpdateSession: (TrainingSession) -> Unit,
    onBackClick: () -> Unit
) {
    var showPlanSelectionDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.session_details)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (trainingSession == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(text = "Training session not found!", color = MaterialTheme.colorScheme.error)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SessionDetailItem(label = stringResource(R.string.client), value = trainingSession.clientName)
                SessionDetailItem(label = stringResource(R.string.date), value = trainingSession.date)
                SessionDetailItem(label = stringResource(R.string.time), value = trainingSession.time)
                SessionDetailItem(label = stringResource(R.string.duration), value = stringResource(R.string.duration_mins, trainingSession.durationMinutes))
                SessionDetailItem(label = stringResource(R.string.type), value = trainingSession.sessionType)
                SessionDetailItem(label = stringResource(R.string.status), value = trainingSession.status)
                
                HorizontalDivider()
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.linked_plan),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (workoutPlan != null) {
                        Row {
                            IconButton(onClick = { showPlanSelectionDialog = true }) {
                                Icon(Icons.Default.Edit, contentDescription = "Change Plan", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(onClick = { onUpdateSession(trainingSession.copy(workoutPlanId = null)) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Plan", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    } else {
                        TextButton(onClick = { showPlanSelectionDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = null)
                            Spacer(Modifier.width(4.dp))
                            Text(stringResource(R.string.link_plan))
                        }
                    }
                }
                
                if (workoutPlan != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onWorkoutPlanClick(workoutPlan.id) },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = workoutPlan.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(R.string.exercises_count, workoutPlan.exercises.size),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (workoutPlan.notes.isNotBlank()) {
                                Text(
                                    text = workoutPlan.notes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            
                            workoutPlan.exercises.take(3).forEach { exercise ->
                                val typeInfo = if (exercise.exerciseType == "TIME") "${exercise.targetDurationSeconds}s" else "${exercise.reps} reps"
                                Text(
                                    text = "• ${exercise.exerciseName}: ${exercise.sets}x $typeInfo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (workoutPlan.exercises.size > 3) {
                                Text(
                                    text = "...and ${workoutPlan.exercises.size - 3} more",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp),
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.no_plan_linked),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                if (trainingSession.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.session_notes),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = trainingSession.notes,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onStartWorkoutClick(trainingSession.id) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = workoutPlan != null
                ) {
                    Text(stringResource(R.string.start_workout))
                }

                OutlinedButton(
                    onClick = { onViewSessionResultsClick(trainingSession.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.view_results))
                }
            }
        }
    }

    if (showPlanSelectionDialog && trainingSession != null) {
        WorkoutPlanSelectionDialog(
            workoutPlans = availableWorkoutPlans,
            onDismiss = { showPlanSelectionDialog = false },
            onSelect = { selectedPlan ->
                onUpdateSession(trainingSession.copy(workoutPlanId = selectedPlan.id))
                showPlanSelectionDialog = false
            }
        )
    }
}

@Composable
fun WorkoutPlanSelectionDialog(
    workoutPlans: List<WorkoutPlan>,
    onDismiss: () -> Unit,
    onSelect: (WorkoutPlan) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.workout_plans)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (workoutPlans.isEmpty()) {
                    Text(stringResource(R.string.no_workout_plans_yet))
                } else {
                    workoutPlans.forEach { plan ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(plan) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = plan.name, fontWeight = FontWeight.Bold)
                                Text(text = stringResource(R.string.exercises_count, plan.exercises.size), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun SessionDetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
