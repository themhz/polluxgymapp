package com.example.personalgymapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsHandball
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.components.AddActionFab
import com.example.personalgymapp.model.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutsScreen(
    exercises: List<Exercise>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedMuscleGroups: Set<String>,
    onMuscleGroupsChange: (Set<String>) -> Unit,
    onExerciseClick: (Int) -> Unit,
    onAddExerciseClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val allMuscleGroups = listOf("Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Cardio")

    val filteredExercises = exercises.filter {
        val matchesSearch = it.name.contains(searchQuery, ignoreCase = true)
        val matchesMuscleGroup = selectedMuscleGroups.isEmpty() || it.muscleGroup in selectedMuscleGroups
        matchesSearch && matchesMuscleGroup
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null
                        )
                        Text("Exercise Library")
                    }
                },
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
        floatingActionButton = {
            AddActionFab(
                label = "Add Exercise",
                onClick = onAddExerciseClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Browse exercises for workout planning",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Search exercises") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Multi-select Dropdown for Muscle Groups
            var expandedFilters by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = if (selectedMuscleGroups.isEmpty()) "All Muscle Groups" else selectedMuscleGroups.joinToString(", "),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filter by Muscle Group") },
                    trailingIcon = {
                        IconButton(onClick = { expandedFilters = !expandedFilters }) {
                            Icon(
                                imageVector = if (expandedFilters) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = if (selectedMuscleGroups.isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { expandedFilters = !expandedFilters }
                )

                DropdownMenu(
                    expanded = expandedFilters,
                    onDismissRequest = { expandedFilters = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    allMuscleGroups.forEach { group ->
                        val isSelected = selectedMuscleGroups.contains(group)
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = null
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(group)
                                }
                            },
                            onClick = {
                                val current = selectedMuscleGroups.toMutableSet()
                                if (isSelected) current.remove(group) else current.add(group)
                                onMuscleGroupsChange(current)
                            }
                        )
                    }
                    if (selectedMuscleGroups.isNotEmpty()) {
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    "OK", 
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                ) 
                            },
                            onClick = {
                                expandedFilters = false
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    "Clear All", 
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                ) 
                            },
                            onClick = {
                                onMuscleGroupsChange(emptySet())
                                expandedFilters = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredExercises.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No exercises found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredExercises) { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onClick = { onExerciseClick(exercise.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, onClick: () -> Unit) {
    val focusIcon = when (exercise.focusArea) {
        "Full Body" -> Icons.Default.AccessibilityNew
        "Upper Body" -> Icons.Default.SportsHandball
        "Lower Body" -> Icons.AutoMirrored.Filled.DirectionsWalk
        else -> Icons.Default.AccessibilityNew
    }

    val typeIcon = when (exercise.trainingType) {
        "Resistance" -> Icons.Default.FitnessCenter
        "Cardio" -> Icons.AutoMirrored.Filled.DirectionsRun
        "Mobility" -> Icons.Default.SelfImprovement
        else -> Icons.Default.FitnessCenter
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(40.dp)
            ) {
                Icon(
                    imageVector = focusIcon,
                    contentDescription = exercise.focusArea,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Icon(
                    imageVector = typeIcon,
                    contentDescription = exercise.trainingType,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SuggestionChip(
                        onClick = { },
                        label = { Text(exercise.focusArea, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.height(24.dp)
                    )
                    SuggestionChip(
                        onClick = { },
                        label = { Text(exercise.trainingType, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.height(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = exercise.muscleGroup,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = exercise.difficulty,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun WorkoutsScreenPreview() {
    com.example.personalgymapp.ui.theme.PersonalGymAppTheme {
        WorkoutsScreen(
            exercises = com.example.personalgymapp.data.mockExercises,
            searchQuery = "",
            onSearchQueryChange = {},
            selectedMuscleGroups = emptySet(),
            onMuscleGroupsChange = {},
            onExerciseClick = {},
            onAddExerciseClick = {},
            onBackClick = {}
        )
    }
}
