package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.model.TrainingSession
import com.example.personalgymapp.model.WorkoutPlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingSessionDetailsScreen(
    trainingSession: TrainingSession?,
    workoutPlan: WorkoutPlan?,
    onWorkoutPlanClick: (Int) -> Unit,
    onViewSessionResultsClick: (Int) -> Unit,
    onStartWorkoutClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Session Details") },
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
                SessionDetailItem(label = "Client", value = trainingSession.clientName)
                SessionDetailItem(label = "Date", value = trainingSession.date)
                SessionDetailItem(label = "Time", value = trainingSession.time)
                SessionDetailItem(label = "Duration", value = "${trainingSession.durationMinutes} minutes")
                SessionDetailItem(label = "Type", value = trainingSession.sessionType)
                SessionDetailItem(label = "Status", value = trainingSession.status)
                
                HorizontalDivider()
                
                Text(
                    text = "Linked Workout Plan",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
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
                                text = "${workoutPlan.exercises.size} Exercises",
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
                            
                            workoutPlan.exercises.forEach { exercise ->
                                val typeInfo = if (exercise.exerciseType == "TIME") "${exercise.targetDurationSeconds}s" else "${exercise.reps} reps"
                                Text(
                                    text = "• ${exercise.exerciseName}: ${exercise.sets}x $typeInfo",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = "No workout plan linked",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                if (trainingSession.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Session Notes",
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
                    Text("Start Workout")
                }

                OutlinedButton(
                    onClick = { onViewSessionResultsClick(trainingSession.id) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Workout Results")
                }
            }
        }
    }
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
