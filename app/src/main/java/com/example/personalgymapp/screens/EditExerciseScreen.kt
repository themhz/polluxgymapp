package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.model.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExerciseScreen(
    exercise: Exercise?,
    onSaveExercise: (Exercise) -> Unit,
    onBackClick: () -> Unit
) {
    if (exercise == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Exercise not found")
        }
        return
    }

    var name by remember { mutableStateOf(exercise.name) }
    var muscleGroup by remember { mutableStateOf(exercise.muscleGroup) }
    var equipment by remember { mutableStateOf(exercise.equipment) }
    var difficulty by remember { mutableStateOf(exercise.difficulty) }
    var instructions by remember { mutableStateOf(exercise.instructions) }
    var focusArea by remember { mutableStateOf(exercise.focusArea) }
    var trainingType by remember { mutableStateOf(exercise.trainingType) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var muscleGroupError by remember { mutableStateOf<String?>(null) }
    var instructionsError by remember { mutableStateOf<String?>(null) }

    val difficulties = listOf("Beginner", "Intermediate", "Advanced")
    val muscleGroups = listOf("Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Cardio")
    val focusAreas = listOf("Upper Body", "Lower Body", "Full Body")
    val trainingTypes = listOf("Resistance", "Cardio", "Mobility")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Exercise") },
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
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                label = { Text("Exercise Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it) } }
            )

            var expandedMuscle by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedMuscle,
                onExpandedChange = { expandedMuscle = !expandedMuscle }
            ) {
                OutlinedTextField(
                    value = muscleGroup,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Muscle Group") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMuscle) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    isError = muscleGroupError != null,
                    supportingText = { muscleGroupError?.let { Text(it) } }
                )
                ExposedDropdownMenu(
                    expanded = expandedMuscle,
                    onDismissRequest = { expandedMuscle = false }
                ) {
                    muscleGroups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group) },
                            onClick = {
                                muscleGroup = group
                                expandedMuscle = false
                                muscleGroupError = null
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = equipment,
                onValueChange = { equipment = it },
                label = { Text("Equipment") },
                modifier = Modifier.fillMaxWidth()
            )

            var expandedDiff by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedDiff,
                onExpandedChange = { expandedDiff = !expandedDiff }
            ) {
                OutlinedTextField(
                    value = difficulty,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Difficulty") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDiff) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedDiff,
                    onDismissRequest = { expandedDiff = false }
                ) {
                    difficulties.forEach { diff ->
                        DropdownMenuItem(
                            text = { Text(diff) },
                            onClick = {
                                difficulty = diff
                                expandedDiff = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it; instructionsError = null },
                label = { Text("Instructions") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                isError = instructionsError != null,
                supportingText = { instructionsError?.let { Text(it) } },
                maxLines = 5
            )

            var expandedFocus by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedFocus,
                onExpandedChange = { expandedFocus = !expandedFocus }
            ) {
                OutlinedTextField(
                    value = focusArea,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Focus Area") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFocus) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedFocus,
                    onDismissRequest = { expandedFocus = false }
                ) {
                    focusAreas.forEach { focus ->
                        DropdownMenuItem(
                            text = { Text(focus) },
                            onClick = {
                                focusArea = focus
                                expandedFocus = false
                            }
                        )
                    }
                }
            }

            var expandedType by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedType,
                onExpandedChange = { expandedType = !expandedType }
            ) {
                OutlinedTextField(
                    value = trainingType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Training Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = { expandedType = false }
                ) {
                    trainingTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                trainingType = type
                                expandedType = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    var isValid = true
                    if (name.isBlank()) {
                        nameError = "Name is required"
                        isValid = false
                    }
                    if (muscleGroup.isBlank()) {
                        muscleGroupError = "Muscle group is required"
                        isValid = false
                    }
                    if (instructions.isBlank()) {
                        instructionsError = "Instructions are required"
                        isValid = false
                    }

                    if (isValid) {
                        onSaveExercise(
                            exercise.copy(
                                name = name,
                                muscleGroup = muscleGroup,
                                equipment = equipment.ifBlank { "None" },
                                difficulty = difficulty,
                                instructions = instructions,
                                focusArea = focusArea,
                                trainingType = trainingType
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Exercise")
            }
        }
    }
}
