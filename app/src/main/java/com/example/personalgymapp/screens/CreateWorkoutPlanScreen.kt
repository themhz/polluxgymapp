package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.R
import com.example.personalgymapp.components.FieldExplanation
import com.example.personalgymapp.model.Exercise
import com.example.personalgymapp.model.WorkoutExercise
import com.example.personalgymapp.model.WorkoutPlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutPlanScreen(
    exercises: List<Exercise>,
    onSaveWorkoutPlan: (WorkoutPlan) -> Unit,
    onBackClick: () -> Unit
) {
    var planName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val selectedExercises = remember { mutableStateListOf<WorkoutExercise>() }

    var planNameError by remember { mutableStateOf<String?>(null) }
    var exercisesError by remember { mutableStateOf<String?>(null) }

    val nameRequiredError = stringResource(R.string.error_plan_name_required)
    val atLeastOneExerciseError = stringResource(R.string.error_at_least_one_exercise)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.create_workout_plan)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cancel))
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
                label = { Text(stringResource(R.string.plan_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                isError = planNameError != null,
                supportingText = { planNameError?.let { Text(it) } },
                trailingIcon = {
                    FieldExplanation(explanation = stringResource(R.string.wp_name_desc))
                }
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.plan_notes_label)) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    FieldExplanation(explanation = stringResource(R.string.wp_notes_desc))
                }
            )

            HorizontalDivider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.exercises),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                FieldExplanation(explanation = stringResource(R.string.wp_exercises_desc))
            }

            if (exercisesError != null) {
                Text(text = exercisesError!!, color = MaterialTheme.colorScheme.error)
            }

            // Display already added exercises
            selectedExercises.forEachIndexed { index, workoutExercise ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = workoutExercise.exerciseName, fontWeight = FontWeight.Bold)
                            IconButton(onClick = { selectedExercises.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = MaterialTheme.colorScheme.error)
                            }
                        }
                        val typeText = if (workoutExercise.exerciseType == "REPS") "${workoutExercise.reps} ${stringResource(R.string.reps).lowercase()}" else "${workoutExercise.targetDurationSeconds}s"
                        val setsRoundsLabel = if (workoutExercise.exerciseType == "REPS") stringResource(R.string.sets) else stringResource(R.string.rounds)
                        val setsRoundsText = "${workoutExercise.sets} $setsRoundsLabel"
                        Text(
                            text = "$setsRoundsText x $typeText | ${workoutExercise.restSeconds}s ${stringResource(R.string.rest_s).lowercase().substringBefore(" ")}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Add Exercise UI
            var showAddDialog by remember { mutableStateOf(false) }
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.add_exercise_to_plan))
                Spacer(Modifier.width(4.dp))
                FieldExplanation(explanation = stringResource(R.string.wp_add_ex_desc))
            }

            if (showAddDialog) {
                AddExerciseToPlanDialog(
                    availableExercises = exercises,
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
                        planNameError = nameRequiredError
                        isValid = false
                    }
                    if (selectedExercises.isEmpty()) {
                        exercisesError = atLeastOneExerciseError
                        isValid = false
                    }

                    if (isValid) {
                        onSaveWorkoutPlan(
                            WorkoutPlan(
                                id = 0, // ID will be assigned in AppNavigation
                                name = planName,
                                notes = notes,
                                exercises = selectedExercises.toList()
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_workout_plan))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseToPlanDialog(
    availableExercises: List<Exercise>,
    onDismiss: () -> Unit,
    onConfirm: (WorkoutExercise) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedEx by remember { mutableStateOf<Exercise?>(null) }
    var exerciseType by remember { mutableStateOf("REPS") }
    var timerType by remember { mutableStateOf("COUNTDOWN") }
    var sets by remember { mutableStateOf("3") }
    var reps by remember { mutableStateOf("10") }
    var duration by remember { mutableStateOf("60") }
    var rest by remember { mutableStateOf("60") }

    val filteredExercises = remember(searchQuery) {
        availableExercises.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.add_exercise_to_plan))
                Spacer(Modifier.width(8.dp))
                FieldExplanation(explanation = stringResource(R.string.wp_add_ex_desc))
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            expanded = true 
                            if (selectedEx?.name != it) selectedEx = null
                        },
                        label = { Text(stringResource(R.string.search_exercises)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        singleLine = true
                    )
                    
                    if (filteredExercises.isNotEmpty()) {
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            filteredExercises.forEach { ex ->
                                DropdownMenuItem(
                                    text = { Text(ex.name) },
                                    onClick = {
                                        selectedEx = ex
                                        searchQuery = ex.name
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    FilterChip(
                        selected = exerciseType == "REPS",
                        onClick = { exerciseType = "REPS" },
                        label = { Text(stringResource(R.string.reps)) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    FilterChip(
                        selected = exerciseType == "TIME",
                        onClick = { exerciseType = "TIME" },
                        label = { Text(stringResource(R.string.time)) },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    FieldExplanation(explanation = stringResource(R.string.wp_ex_type_desc))
                }

                if (exerciseType == "TIME") {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        FilterChip(
                            selected = timerType == "COUNTDOWN",
                            onClick = { timerType = "COUNTDOWN" },
                            label = { Text(stringResource(R.string.countdown)) },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        FilterChip(
                            selected = timerType == "COUNTUP",
                            onClick = { timerType = "COUNTUP" },
                            label = { Text(stringResource(R.string.countup)) },
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        FieldExplanation(explanation = stringResource(R.string.wp_timer_type_desc))
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = sets,
                        onValueChange = { sets = it },
                        label = { Text(if (exerciseType == "REPS") stringResource(R.string.sets) else stringResource(R.string.rounds)) },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        trailingIcon = { FieldExplanation(explanation = stringResource(R.string.wp_sets_desc)) }
                    )
                    if (exerciseType == "REPS") {
                        OutlinedTextField(
                            value = reps,
                            onValueChange = { reps = it },
                            label = { Text(stringResource(R.string.reps)) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon = { FieldExplanation(explanation = stringResource(R.string.wp_reps_val_desc)) }
                        )
                    } else {
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text(stringResource(R.string.duration_s)) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon = { FieldExplanation(explanation = stringResource(R.string.wp_duration_desc)) }
                        )
                    }
                }
                OutlinedTextField(
                    value = rest,
                    onValueChange = { rest = it },
                    label = { Text(stringResource(R.string.rest_s)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    trailingIcon = { FieldExplanation(explanation = stringResource(R.string.wp_rest_desc)) }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val s = sets.toIntOrNull() ?: 0
                    val r = if (exerciseType == "REPS") (reps.toIntOrNull() ?: 0) else 0
                    val d = if (exerciseType == "TIME") (duration.toIntOrNull() ?: 0) else null
                    val res = rest.toIntOrNull() ?: 0
                    if (selectedEx != null && s > 0 && (r > 0 || (d != null && d > 0)) && res >= 0) {
                        onConfirm(
                            WorkoutExercise(
                                exerciseId = selectedEx!!.id,
                                exerciseName = selectedEx!!.name,
                                sets = s,
                                reps = r,
                                restSeconds = res,
                                exerciseType = exerciseType,
                                targetDurationSeconds = d,
                                timerType = timerType
                            )
                        )
                    }
                }
            ) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
