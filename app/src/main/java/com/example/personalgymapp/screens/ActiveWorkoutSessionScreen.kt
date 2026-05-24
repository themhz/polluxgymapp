package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.model.*
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutSessionScreen(
    trainingSession: TrainingSession?,
    workoutPlan: WorkoutPlan?,
    onFinishWorkout: (List<SessionExerciseResult>) -> Unit,
    onCancelWorkout: () -> Unit
) {
    if (trainingSession == null) {
        ErrorScreen("Training session not found", onCancelWorkout)
        return
    }
    if (workoutPlan == null) {
        ErrorScreen("No workout plan linked to this session", onCancelWorkout)
        return
    }

    var currentExerciseIndex by remember { mutableStateOf(0) }
    var currentSetNumber by remember { mutableStateOf(1) }
    
    val allResults = remember { mutableStateListOf<SessionExerciseResult>() }
    val currentExerciseSets = remember { mutableStateListOf<SessionSetResult>() }

    val currentExercise = workoutPlan.exercises[currentExerciseIndex]
    
    // Reps/Weight/Notes input state
    var repsInput by remember { mutableStateOf(currentExercise.reps.toString()) }
    var weightInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }

    // Timer state
    var timerSeconds by remember { mutableStateOf(currentExercise.targetDurationSeconds ?: 0) }
    var isTimerRunning by remember { mutableStateOf(false) }

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && timerSeconds > 0) {
            delay(1000L)
            timerSeconds -= 1
            if (timerSeconds == 0) isTimerRunning = false
        }
    }

    // Reset inputs when moving to a new exercise or set
    LaunchedEffect(currentExerciseIndex, currentSetNumber) {
        repsInput = workoutPlan.exercises[currentExerciseIndex].reps.toString()
        timerSeconds = workoutPlan.exercises[currentExerciseIndex].targetDurationSeconds ?: 0
        isTimerRunning = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Active Workout") },
                actions = {
                    IconButton(onClick = onCancelWorkout) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Client: ${trainingSession.clientName}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = "Exercise ${currentExerciseIndex + 1} of ${workoutPlan.exercises.size}",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = currentExercise.exerciseName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Target: ${currentExercise.sets} sets", fontWeight = FontWeight.Bold)
                    if (currentExercise.exerciseType == "REPS") {
                        Text(text = "Goal: ${currentExercise.reps} reps")
                    } else {
                        Text(text = "Goal: ${currentExercise.targetDurationSeconds} seconds")
                    }
                    Text(text = "Rest: ${currentExercise.restSeconds}s")
                }
            }

            Text(
                text = "Set $currentSetNumber",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (currentExercise.exerciseType == "REPS") {
                OutlinedTextField(
                    value = repsInput,
                    onValueChange = { repsInput = it },
                    label = { Text("Reps Completed") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = String.format(Locale.getDefault(), "%02d:%02d", timerSeconds / 60, timerSeconds % 60),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { isTimerRunning = !isTimerRunning },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTimerRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(if (isTimerRunning) "Stop Timer" else "Start Timer")
                    }
                    Button(onClick = { timerSeconds = currentExercise.targetDurationSeconds ?: 0 }) {
                        Text("Reset")
                    }
                }
            }

            OutlinedTextField(
                value = notesInput,
                onValueChange = { notesInput = it },
                label = { Text("Set Notes (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val reps = repsInput.toIntOrNull()
                    val weight = weightInput.toDoubleOrNull()
                    val duration = if (currentExercise.exerciseType == "TIME") 
                        (currentExercise.targetDurationSeconds ?: 0) - timerSeconds else null

                    currentExerciseSets.add(
                        SessionSetResult(
                            setNumber = currentSetNumber,
                            reps = reps,
                            weightKg = weight,
                            durationSeconds = duration,
                            notes = notesInput
                        )
                    )

                    if (currentSetNumber < currentExercise.sets) {
                        currentSetNumber++
                        weightInput = "" // Usually weight stays the same, but for variety...
                        notesInput = ""
                    } else {
                        // All sets for this exercise done
                        allResults.add(
                            SessionExerciseResult(
                                id = 0, // Assigned on finish
                                trainingSessionId = trainingSession.id,
                                exerciseId = currentExercise.exerciseId,
                                exerciseName = currentExercise.exerciseName,
                                sets = currentExerciseSets.toList(),
                                notes = ""
                            )
                        )
                        currentExerciseSets.clear()
                        
                        if (currentExerciseIndex < workoutPlan.exercises.size - 1) {
                            currentExerciseIndex++
                            currentSetNumber = 1
                            weightInput = ""
                            notesInput = ""
                        } else {
                            // Workout Finished
                            onFinishWorkout(allResults.toList())
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = if (currentExercise.exerciseType == "REPS") repsInput.toIntOrNull() != null else true
            ) {
                val buttonText = if (currentExerciseIndex == workoutPlan.exercises.size - 1 && currentSetNumber == currentExercise.sets) 
                    "Finish Workout" else "Complete Set"
                Text(buttonText)
            }
        }
    }
}

@Composable
fun ErrorScreen(message: String, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = message, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) { Text("Back") }
        }
    }
}
