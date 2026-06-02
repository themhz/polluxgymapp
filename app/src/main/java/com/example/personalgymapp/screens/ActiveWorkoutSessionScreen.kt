package com.example.personalgymapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

    var isStarted by remember { mutableStateOf(false) }
    var currentExerciseIndex by remember { mutableStateOf(0) }
    
    // State for all exercises: Map<ExerciseIndex, List<SetResults>>
    val sessionProgress = remember { mutableStateMapOf<Int, MutableList<SessionSetResult>>() }
    
    // Initialize session progress with empty lists
    LaunchedEffect(workoutPlan) {
        workoutPlan.exercises.forEachIndexed { index, _ ->
            if (!sessionProgress.containsKey(index)) {
                sessionProgress[index] = mutableListOf()
            }
        }
    }

    val currentExercise = workoutPlan.exercises[currentExerciseIndex]
    val currentExerciseSets = sessionProgress[currentExerciseIndex] ?: mutableListOf()
    val isExerciseFinished = currentExerciseSets.size >= currentExercise.sets

    // Input state
    var repsInput by remember { mutableStateOf(currentExercise.reps.toString()) }
    var weightInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }

    // Timer and Mode state
    var isResting by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Helper to add a set result
    val completeSet = {
        val reps = repsInput.toIntOrNull()
        val weight = weightInput.toDoubleOrNull()
        val duration = if (currentExercise.exerciseType == "TIME")
            (currentExercise.targetDurationSeconds ?: 0) - (if (isResting) 0 else timerSeconds) else null

        val newSet = SessionSetResult(
            setNumber = currentExerciseSets.size + 1,
            reps = reps,
            weightKg = weight,
            durationSeconds = duration,
            notes = notesInput
        )

        val updatedList = sessionProgress[currentExerciseIndex]?.toMutableList() ?: mutableListOf()
        updatedList.add(newSet)
        sessionProgress[currentExerciseIndex] = updatedList
        
        notesInput = ""
        
        // Handle rest or next set
        if (updatedList.size < currentExercise.sets && currentExercise.restSeconds > 0) {
            isResting = true
            timerSeconds = currentExercise.restSeconds
            isTimerRunning = true
        } else {
            isResting = false
            isTimerRunning = false
        }
    }

    // Timer Logic
    LaunchedEffect(isTimerRunning, isResting) {
        while (isTimerRunning && timerSeconds > 0) {
            delay(1000L)
            timerSeconds -= 1
        }
        if (isTimerRunning && timerSeconds == 0) {
            if (isResting) {
                // Rest finished, log the actual rest time taken
                val actualRest = currentExercise.restSeconds - timerSeconds
                val updatedList = sessionProgress[currentExerciseIndex]?.toMutableList() ?: mutableListOf()
                if (updatedList.isNotEmpty()) {
                    val lastSet = updatedList.last()
                    updatedList[updatedList.size - 1] = lastSet.copy(restSecondsDone = actualRest)
                    sessionProgress[currentExerciseIndex] = updatedList
                }

                isResting = false
                if (currentExercise.exerciseType == "TIME") {
                    timerSeconds = currentExercise.targetDurationSeconds ?: 0
                    isTimerRunning = true
                } else {
                    isTimerRunning = false
                }
            } else if (currentExercise.exerciseType == "TIME") {
                // Exercise time finished, complete set automatically
                completeSet()
            }
        }
    }

    // Reset/Setup timer when moving to a new exercise
    LaunchedEffect(currentExerciseIndex) {
        isResting = false
        isTimerRunning = false
        repsInput = workoutPlan.exercises[currentExerciseIndex].reps.toString()
        timerSeconds = if (workoutPlan.exercises[currentExerciseIndex].exerciseType == "TIME")
            workoutPlan.exercises[currentExerciseIndex].targetDurationSeconds ?: 0 else 0
        weightInput = ""
        notesInput = ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Active Workout") },
                actions = {
                    if (isStarted) {
                        IconButton(onClick = {
                            sessionProgress.clear()
                            workoutPlan.exercises.forEachIndexed { index, _ -> sessionProgress[index] = mutableListOf() }
                            currentExerciseIndex = 0
                            isStarted = false
                        }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Restart Workout")
                        }
                    }
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Exercise Progress Header
            ExerciseProgressHeader(
                exerciseCount = workoutPlan.exercises.size,
                currentIndex = currentExerciseIndex,
                completedIndices = sessionProgress.filter { it.value.size >= workoutPlan.exercises[it.key].sets }.keys.toSet(),
                onIndexClick = { if (isStarted) currentExerciseIndex = it }
            )

            Text(
                text = "Client: ${trainingSession.clientName}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            
            // Exercise Navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (currentExerciseIndex > 0) currentExerciseIndex-- },
                    enabled = isStarted && currentExerciseIndex > 0
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentExercise.exerciseName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (isStarted && !isExerciseFinished) {
                        Text(
                            text = if (isResting) "Next: Set ${currentExerciseSets.size + 1}" else "Set ${currentExerciseSets.size + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(
                    onClick = { if (currentExerciseIndex < workoutPlan.exercises.size - 1) currentExerciseIndex++ },
                    enabled = isStarted && currentExerciseIndex < workoutPlan.exercises.size - 1
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next")
                }
            }

            // Target Info Card
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "Goal: ${currentExercise.sets} sets", fontWeight = FontWeight.Bold)
                    val goalText = if (currentExercise.exerciseType == "REPS") "${currentExercise.reps} reps" else "${currentExercise.targetDurationSeconds}s"
                    Text(text = "Target: $goalText | Rest: ${currentExercise.restSeconds}s")
                }
            }

            if (!isStarted) {
                Button(
                    onClick = { 
                        isStarted = true 
                        if (currentExercise.exerciseType == "TIME") {
                            timerSeconds = currentExercise.targetDurationSeconds ?: 0
                            isTimerRunning = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                ) {
                    Text("Start the Session")
                }
            } else {
                // Timer / Rest Display
                if (isResting || currentExercise.exerciseType == "TIME") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isResting) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isResting) "RESTING..." else "EXERCISE TIME",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (isResting) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                            )
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
                                    Icon(if (isTimerRunning) Icons.Default.Close else Icons.Default.PlayArrow, contentDescription = null)
                                    Spacer(Modifier.width(4.dp))
                                    Text(if (isTimerRunning) "Pause" else "Resume")
                                }
                                if (isResting) {
                                    TextButton(onClick = { 
                                        val actualRest = currentExercise.restSeconds - timerSeconds
                                        val updatedList = sessionProgress[currentExerciseIndex]?.toMutableList() ?: mutableListOf()
                                        if (updatedList.isNotEmpty()) {
                                            val lastSet = updatedList.last()
                                            updatedList[updatedList.size - 1] = lastSet.copy(restSecondsDone = actualRest)
                                            sessionProgress[currentExerciseIndex] = updatedList
                                        }
                                        timerSeconds = 0
                                        isTimerRunning = true 
                                    }) {
                                        Text("Skip Rest")
                                    }
                                }
                            }
                        }
                    }
                }

                // Set History for current exercise
                if (currentExerciseSets.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Recorded Sets:", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                        currentExerciseSets.forEach { set ->
                            val resText = if (set.reps != null) "${set.reps} reps" else "${set.durationSeconds}s"
                            val weightText = if (set.weightKg != null) " @ ${set.weightKg}kg" else ""
                            Text(text = "Set ${set.setNumber}: $resText$weightText", style = MaterialTheme.typography.bodySmall)
                        }
                        
                        TextButton(
                            onClick = { sessionProgress[currentExerciseIndex] = mutableListOf() },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Reset Exercise Progress")
                        }
                    }
                }

                if (isExerciseFinished) {
                    Text(
                        text = "Exercise Completed! ✅",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    
                    if (currentExerciseIndex < workoutPlan.exercises.size - 1) {
                        Button(onClick = { currentExerciseIndex++ }, modifier = Modifier.fillMaxWidth()) {
                            Text("Go to Next Exercise")
                        }
                    } else {
                        Button(
                            onClick = {
                                val finalResults = workoutPlan.exercises.mapIndexed { index, exercise ->
                                    SessionExerciseResult(
                                        id = 0,
                                        trainingSessionId = trainingSession.id,
                                        exerciseId = exercise.exerciseId,
                                        exerciseName = exercise.exerciseName,
                                        sets = sessionProgress[index] ?: listOf(),
                                        notes = ""
                                    )
                                }
                                onFinishWorkout(finalResults)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Finish Workout")
                        }
                    }
                } else {
                    // Entry Form for next set
                    if (!isResting) {
                        if (currentExercise.exerciseType == "REPS") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = repsInput,
                                    onValueChange = { repsInput = it },
                                    label = { Text("Reps") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = weightInput,
                                    onValueChange = { weightInput = it },
                                    label = { Text("Weight (kg)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            label = { Text("Set Notes (Optional)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = { completeSet() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = if (currentExercise.exerciseType == "REPS") repsInput.toIntOrNull() != null else true
                        ) {
                            Text("Complete Set ${currentExerciseSets.size + 1}")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ExerciseProgressHeader(
    exerciseCount: Int,
    currentIndex: Int,
    completedIndices: Set<Int>,
    onIndexClick: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(exerciseCount) { index ->
            val isCompleted = completedIndices.contains(index)
            val isCurrent = index == currentIndex
            
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCurrent -> MaterialTheme.colorScheme.primary
                            isCompleted -> MaterialTheme.colorScheme.primaryContainer
                            else -> MaterialTheme.colorScheme.surface
                        }
                    )
                    .clickable { onIndexClick(index) },
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = if (isCurrent) Color.White else MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = (index + 1).toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
            
            if (index < exerciseCount - 1) {
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
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
