package com.example.personalgymapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.SportsHandball
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.personalgymapp.R
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
    val muscleGroupMap = mapOf(
        "Chest" to stringResource(R.string.mg_chest),
        "Back" to stringResource(R.string.mg_back),
        "Legs" to stringResource(R.string.mg_legs),
        "Shoulders" to stringResource(R.string.mg_shoulders),
        "Arms" to stringResource(R.string.mg_arms),
        "Core" to stringResource(R.string.mg_core),
        "Cardio" to stringResource(R.string.mg_cardio)
    )

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
                        Text(stringResource(R.string.exercise_library))
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
                label = stringResource(R.string.add_exercise),
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
                text = stringResource(R.string.browse_exercises),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text(stringResource(R.string.search_exercises)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Multi-select Dropdown for Muscle Groups
            var expandedFilters by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                val selectedText = if (selectedMuscleGroups.isEmpty()) {
                    stringResource(R.string.all_muscle_groups)
                } else {
                    selectedMuscleGroups.map { muscleGroupMap[it] ?: it }.joinToString(", ")
                }

                OutlinedTextField(
                    value = selectedText,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.filter_by_muscle_group)) },
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
                    muscleGroupMap.forEach { (key, label) ->
                        val isSelected = selectedMuscleGroups.contains(key)
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
                                    Text(label)
                                }
                            },
                            onClick = {
                                val current = selectedMuscleGroups.toMutableSet()
                                if (isSelected) current.remove(key) else current.add(key)
                                onMuscleGroupsChange(current)
                            }
                        )
                    }
                    if (selectedMuscleGroups.isNotEmpty()) {
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    stringResource(R.string.ok), 
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
                                    stringResource(R.string.clear_all), 
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
                        text = stringResource(R.string.no_exercises_found),
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
                            onClick = { onExerciseClick(exercise.id) },
                            muscleGroupLabel = muscleGroupMap[exercise.muscleGroup] ?: exercise.muscleGroup
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, onClick: () -> Unit, muscleGroupLabel: String) {
    val context = LocalContext.current
    
    // Determine the image model for Coil
    val imageModel = remember(exercise.imageResName, exercise.imageUri) {
        if (exercise.imageUri != null) {
            exercise.imageUri
        } else if (exercise.imageResName != null) {
            context.resources.getIdentifier(exercise.imageResName, "drawable", context.packageName).let {
                if (it != 0) it else null
            }
        } else null
    }

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
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exercise Image Thumbnail
            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Fallback icons if no image
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(80.dp)
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
                        text = muscleGroupLabel,
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
            exercises = emptyList(),
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
