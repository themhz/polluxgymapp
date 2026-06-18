package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.model.Exercise
import com.example.personalgymapp.model.WorkoutExercise
import com.example.personalgymapp.model.WorkoutPlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWorkoutPlanScreen(
    workoutPlan: WorkoutPlan?,
    availableExercises: List<Exercise>,
    onSaveWorkoutPlan: (WorkoutPlan) -> Unit,
    onDeleteWorkoutPlan: (WorkoutPlan) -> Unit,
    onBackClick: () -> Unit
) {
    if (workoutPlan == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Workout Plan not found")
        }
        return
    }

    var planName by remember { mutableStateOf(workoutPlan.name) }
    var notes by remember { mutableStateOf(workoutPlan.notes) }
    val selectedExercises = remember { mutableStateListOf<WorkoutExercise>().apply { addAll(workoutPlan.exercises) } }

    var planNameError by remember { mutableStateOf<String?>(null) }
    var exercisesError by remember { mutableStateOf<String?>(null) }
    var showDeletePlanDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Plan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeletePlanDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Plan", tint = MaterialTheme.colorScheme.error)
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
            OutlinedTextField(
                value = planName,
                onValueChange = { planName = it; planNameError = null },
                label = { Text("Plan Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = planNameError != null,
                supportingText = { planNameError?.let { Text(it) } }
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            Text(
                text = "Exercises",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            if (exercisesError != null) {
                Text(text = exercisesError!!, color = MaterialTheme.colorScheme.error)
            }

            // List of exercises in the plan
            selectedExercises.forEachIndexed { index, workoutExercise ->
                var showEditExDialog by remember { mutableStateOf(false) }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = workoutExercise.exerciseName, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            Row {
                                IconButton(onClick = { showEditExDialog = true }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit Exercise", tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { selectedExercises.removeAt(index) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        val typeText = if (workoutExercise.exerciseType == "REPS") "${workoutExercise.reps} reps" else "${workoutExercise.targetDurationSeconds}s"
                        Text(
                            text = "${workoutExercise.sets} sets x $typeText | ${workoutExercise.restSeconds}s rest",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (showEditExDialog) {
                    // Reusing the Add Dialog logic for editing within the list
                    EditExerciseInPlanDialog(
                        workoutExercise = workoutExercise,
                        onDismiss = { showEditExDialog = false },
                        onConfirm = { updated ->
                            selectedExercises[index] = updated
                            showEditExDialog = false
                        }
                    )
                }
            }

            // Add Exercise Button
            var showAddDialog by remember { mutableStateOf(false) }
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Exercise")
            }

            if (showAddDialog) {
                AddExerciseToPlanDialog(
                    availableExercises = availableExercises,
                    onDismiss = { showAddDialog = false },
                    onConfirm = { workoutExercise ->
                        selectedExercises.add(workoutExercise)
                        exercisesError = null
                        showAddDialog = false
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    var isValid = true
                    if (planName.isBlank()) {
                        planNameError = "Plan name is required"
                        isValid = false
                    }
                    if (selectedExercises.isEmpty()) {
                        exercisesError = "Add at least one exercise"
                        isValid = false
                    }

                    if (isValid) {
                        onSaveWorkoutPlan(
                            workoutPlan.copy(
                                name = planName,
                                notes = notes,
                                exercises = selectedExercises.toList()
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

    if (showDeletePlanDialog) {
        AlertDialog(
            onDismissRequest = { showDeletePlanDialog = false },
            title = { Text("Delete Workout Plan") },
            text = { Text("Are you sure you want to permanently delete '${workoutPlan.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteWorkoutPlan(workoutPlan)
                        showDeletePlanDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeletePlanDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExerciseInPlanDialog(
    workoutExercise: WorkoutExercise,
    onDismiss: () -> Unit,
    onConfirm: (WorkoutExercise) -> Unit
) {
    var exerciseType by remember { mutableStateOf(workoutExercise.exerciseType) }
    var sets by remember { mutableStateOf(workoutExercise.sets.toString()) }
    var reps by remember { mutableStateOf(workoutExercise.reps.toString()) }
    var duration by remember { mutableStateOf(workoutExercise.targetDurationSeconds?.toString() ?: "60") }
    var rest by remember { mutableStateOf(workoutExercise.restSeconds.toString()) }
    var isGpsEnabled by remember { mutableStateOf(workoutExercise.isGpsEnabled) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit ${workoutExercise.exerciseName}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        modifier = Modifier.weight(1f)
                    )
                    if (exerciseType == "REPS") {
                        OutlinedTextField(
                            value = reps,
                            onValueChange = { reps = it },
                            label = { Text("Reps") },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text("Dur (s)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                OutlinedTextField(
                    value = rest,
                    onValueChange = { rest = it },
                    label = { Text("Rest (s)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.AutoMirrored.Filled.DirectionsRun, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text("Enable GPS Tracking", style = MaterialTheme.typography.bodyMedium)
                    }
                    Switch(
                        checked = isGpsEnabled,
                        onCheckedChange = { isGpsEnabled = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val s = sets.toIntOrNull() ?: 0
                val r = reps.toIntOrNull() ?: 0
                val d = duration.toIntOrNull() ?: 0
                val res = rest.toIntOrNull() ?: 0
                onConfirm(workoutExercise.copy(
                    sets = s, 
                    reps = if(exerciseType == "REPS") r else 0,
                    targetDurationSeconds = if(exerciseType == "TIME") d else null,
                    restSeconds = res,
                    exerciseType = exerciseType,
                    isGpsEnabled = isGpsEnabled
                ))
            }) { Text("Update") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
