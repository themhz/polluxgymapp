package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.model.Exercise
import com.example.personalgymapp.model.SessionExerciseResult
import com.example.personalgymapp.model.SessionSetResult
import com.example.personalgymapp.model.TrainingSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSessionExerciseResultScreen(
    trainingSession: TrainingSession?,
    exercises: List<Exercise>,
    onSaveExerciseResult: (SessionExerciseResult) -> Unit,
    onBackClick: () -> Unit
) {
    if (trainingSession == null) {
        Scaffold { innerPadding ->
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Training session not found")
            }
        }
        return
    }

    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
    var exerciseNotes by remember { mutableStateOf("") }
    var numSetsStr by remember { mutableStateOf("3") }
    
    // Using a list of mutable states for each set
    // Each element is (reps, weight, duration, notes)
    val setResults = remember { mutableStateListOf<SetInputState>() }
    
    // Update the setResults list when numSetsStr changes
    LaunchedEffect(numSetsStr) {
        val n = numSetsStr.toIntOrNull() ?: 0
        if (n > 0) {
            while (setResults.size < n) {
                setResults.add(SetInputState())
            }
            while (setResults.size > n) {
                setResults.removeAt(setResults.size - 1)
            }
        }
    }

    var exerciseError by remember { mutableStateOf<String?>(null) }
    var setsError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Exercise Result") },
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
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Exercise Selection
            var expandedExercise by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedExercise,
                onExpandedChange = { expandedExercise = !expandedExercise }
            ) {
                OutlinedTextField(
                    value = selectedExercise?.name ?: "Select Exercise",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Exercise") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedExercise) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    isError = exerciseError != null,
                    supportingText = { exerciseError?.let { Text(it) } }
                )
                ExposedDropdownMenu(
                    expanded = expandedExercise,
                    onDismissRequest = { expandedExercise = false }
                ) {
                    exercises.forEach { ex ->
                        DropdownMenuItem(
                            text = { Text(ex.name) },
                            onClick = {
                                selectedExercise = ex
                                expandedExercise = false
                                exerciseError = null
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = numSetsStr,
                onValueChange = { 
                    numSetsStr = it
                    setsError = null
                },
                label = { Text("Number of Sets") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = setsError != null,
                supportingText = { setsError?.let { Text(it) } }
            )

            OutlinedTextField(
                value = exerciseNotes,
                onValueChange = { exerciseNotes = it },
                label = { Text("Exercise Notes (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            setResults.forEachIndexed { index, state ->
                Text(
                    text = "Set ${index + 1}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.reps,
                        onValueChange = { state.reps = it },
                        label = { Text("Reps") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = state.weight,
                        onValueChange = { state.weight = it },
                        label = { Text("Weight (kg)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                
                OutlinedTextField(
                    value = state.duration,
                    onValueChange = { state.duration = it },
                    label = { Text("Duration (seconds)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                
                OutlinedTextField(
                    value = state.notes,
                    onValueChange = { state.notes = it },
                    label = { Text("Set Notes (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    var isValid = true
                    if (selectedExercise == null) {
                        exerciseError = "Please select an exercise"
                        isValid = false
                    }
                    val n = numSetsStr.toIntOrNull() ?: 0
                    if (n <= 0) {
                        setsError = "Enter a valid number of sets"
                        isValid = false
                    }

                    // Validate each set
                    val validatedSets = mutableListOf<SessionSetResult>()
                    setResults.forEachIndexed { index, state ->
                        val r = state.reps.toIntOrNull()
                        val w = state.weight.toDoubleOrNull()
                        val d = state.duration.toIntOrNull()
                        
                        if (r == null && d == null) {
                            // At least reps or duration required
                            isValid = false
                        }
                        
                        if (isValid) {
                            validatedSets.add(SessionSetResult(
                                setNumber = index + 1,
                                reps = r,
                                weightKg = w,
                                durationSeconds = d,
                                notes = state.notes
                            ))
                        }
                    }

                    if (isValid) {
                        onSaveExerciseResult(
                            SessionExerciseResult(
                                id = 0, // Assigned in navigation
                                trainingSessionId = trainingSession.id,
                                exerciseId = selectedExercise!!.id,
                                exerciseName = selectedExercise!!.name,
                                sets = validatedSets,
                                notes = exerciseNotes
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Exercise Result")
            }
        }
    }
}

class SetInputState {
    var reps by mutableStateOf("")
    var weight by mutableStateOf("")
    var duration by mutableStateOf("")
    var notes by mutableStateOf("")
}
