package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.model.WorkoutExercise
import com.example.personalgymapp.model.WorkoutPlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWorkoutExerciseScreen(
    workoutPlan: WorkoutPlan?,
    exerciseId: Int,
    onSave: (WorkoutExercise) -> Unit,
    onBackClick: () -> Unit
) {
    val workoutExercise = workoutPlan?.exercises?.find { it.exerciseId == exerciseId }

    if (workoutPlan == null || workoutExercise == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Workout exercise not found")
        }
        return
    }

    var exerciseType by remember { mutableStateOf(workoutExercise.exerciseType) }
    var sets by remember { mutableStateOf(workoutExercise.sets.toString()) }
    var reps by remember { mutableStateOf(workoutExercise.reps.toString()) }
    var duration by remember { mutableStateOf(workoutExercise.targetDurationSeconds?.toString() ?: "") }
    var rest by remember { mutableStateOf(workoutExercise.restSeconds.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit ${workoutExercise.exerciseName}") },
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Workout Plan: ${workoutPlan.name}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            HorizontalDivider()

            Row(modifier = Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = exerciseType == "REPS",
                    onClick = { exerciseType = "REPS" },
                    label = { Text("Reps") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                FilterChip(
                    selected = exerciseType == "TIME",
                    onClick = { exerciseType = "TIME" },
                    label = { Text("Time") },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = sets,
                    onValueChange = { sets = it },
                    label = { Text("Sets") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (exerciseType == "REPS") {
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { reps = it },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                } else {
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Duration (s)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            OutlinedTextField(
                value = rest,
                onValueChange = { rest = it },
                label = { Text("Rest (s)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    val s = sets.toIntOrNull() ?: 0
                    val r = if (exerciseType == "REPS") (reps.toIntOrNull() ?: 0) else 0
                    val d = if (exerciseType == "TIME") (duration.toIntOrNull() ?: 0) else null
                    val res = rest.toIntOrNull() ?: 0

                    if (s > 0 && (r > 0 || (d != null && d > 0)) && res >= 0) {
                        onSave(
                            workoutExercise.copy(
                                sets = s,
                                reps = r,
                                restSeconds = res,
                                exerciseType = exerciseType,
                                targetDurationSeconds = d
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}
