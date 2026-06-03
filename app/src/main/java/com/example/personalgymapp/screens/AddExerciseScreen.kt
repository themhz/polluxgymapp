package com.example.personalgymapp.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.personalgymapp.R
import com.example.personalgymapp.model.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseScreen(
    nextId: Int,
    onSaveExercise: (Exercise) -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var muscleGroup by remember { mutableStateOf("") }
    var equipment by remember { mutableStateOf("") }
    var difficulty by remember { mutableStateOf("Beginner") }
    var instructions by remember { mutableStateOf("") }
    var focusArea by remember { mutableStateOf("Upper Body") }
    var trainingType by remember { mutableStateOf("Resistance") }
    var imageResName by remember { mutableStateOf("") }
    var videoResName by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var selectedVideoUri by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri?.toString()
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedVideoUri = uri?.toString()
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    var nameError by remember { mutableStateOf<String?>(null) }
    var muscleGroupError by remember { mutableStateOf<String?>(null) }
    var instructionsError by remember { mutableStateOf<String?>(null) }

    val difficulties = mapOf(
        "Beginner" to stringResource(R.string.diff_beginner),
        "Intermediate" to stringResource(R.string.diff_intermediate),
        "Advanced" to stringResource(R.string.diff_advanced)
    )
    val muscleGroups = mapOf(
        "Chest" to stringResource(R.string.mg_chest),
        "Back" to stringResource(R.string.mg_back),
        "Legs" to stringResource(R.string.mg_legs),
        "Shoulders" to stringResource(R.string.mg_shoulders),
        "Arms" to stringResource(R.string.mg_arms),
        "Core" to stringResource(R.string.mg_core),
        "Cardio" to stringResource(R.string.mg_cardio)
    )
    val focusAreas = mapOf(
        "Upper Body" to stringResource(R.string.focus_upper),
        "Lower Body" to stringResource(R.string.focus_lower),
        "Full Body" to stringResource(R.string.focus_full)
    )
    val trainingTypes = mapOf(
        "Resistance" to stringResource(R.string.tt_resistance),
        "Cardio" to stringResource(R.string.tt_cardio),
        "Mobility" to stringResource(R.string.tt_mobility)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_exercise)) },
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
                label = { Text(stringResource(R.string.exercise_name)) },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it) } }
            )

            // Muscle Group selection
            var expandedMuscle by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedMuscle,
                onExpandedChange = { expandedMuscle = !expandedMuscle }
            ) {
                OutlinedTextField(
                    value = muscleGroups[muscleGroup] ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.muscle_group)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMuscle) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    isError = muscleGroupError != null,
                    supportingText = { muscleGroupError?.let { Text(it) } }
                )
                ExposedDropdownMenu(
                    expanded = expandedMuscle,
                    onDismissRequest = { expandedMuscle = false }
                ) {
                    muscleGroups.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                muscleGroup = key
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
                label = { Text(stringResource(R.string.equipment)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.equipment_hint)) }
            )

            var expandedDiff by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedDiff,
                onExpandedChange = { expandedDiff = !expandedDiff }
            ) {
                OutlinedTextField(
                    value = difficulties[difficulty] ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.difficulty)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDiff) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedDiff,
                    onDismissRequest = { expandedDiff = false }
                ) {
                    difficulties.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                difficulty = key
                                expandedDiff = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = instructions,
                onValueChange = { instructions = it; instructionsError = null },
                label = { Text(stringResource(R.string.instructions)) },
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
                    value = focusAreas[focusArea] ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.focus_area)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFocus) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedFocus,
                    onDismissRequest = { expandedFocus = false }
                ) {
                    focusAreas.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                focusArea = key
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
                    value = trainingTypes[trainingType] ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.training_type)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedType) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedType,
                    onDismissRequest = { expandedType = false }
                ) {
                    trainingTypes.forEach { (key, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                trainingType = key
                                expandedType = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = imageResName,
                onValueChange = { imageResName = it },
                label = { Text(stringResource(R.string.image_res)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. squats") }
            )

            // Image Picker Button
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = stringResource(R.string.external_image), style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(8.dp))
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(bottom = 8.dp)
                        )
                    }
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (selectedImageUri == null) stringResource(R.string.select_image) else stringResource(R.string.change_image))
                    }
                }
            }

            OutlinedTextField(
                value = videoResName,
                onValueChange = { videoResName = it },
                label = { Text(stringResource(R.string.video_res)) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. running") }
            )

            // Video Picker Button
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = stringResource(R.string.external_video), style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(8.dp))
                    if (selectedVideoUri != null) {
                        Text(
                            text = "Video selected: ${selectedVideoUri!!.takeLast(30)}...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Button(
                        onClick = { videoPickerLauncher.launch("video/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (selectedVideoUri == null) stringResource(R.string.select_video) else stringResource(R.string.change_video))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    var isValid = true
                    if (name.isBlank()) {
                        nameError = context.getString(R.string.error_name_required)
                        isValid = false
                    }
                    if (muscleGroup.isBlank()) {
                        muscleGroupError = context.getString(R.string.error_mg_required)
                        isValid = false
                    }
                    if (instructions.isBlank()) {
                        instructionsError = context.getString(R.string.error_instr_required)
                        isValid = false
                    }

                    if (isValid) {
                        val newExercise = Exercise(
                            id = nextId,
                            name = name,
                            muscleGroup = muscleGroup,
                            equipment = equipment.ifBlank { "None" },
                            difficulty = difficulty,
                            instructions = instructions,
                            focusArea = focusArea,
                            trainingType = trainingType,
                            imageResName = imageResName.ifBlank { null },
                            videoResName = videoResName.ifBlank { null },
                            imageUri = selectedImageUri,
                            videoUri = selectedVideoUri
                        )
                        onSaveExercise(newExercise)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_exercise))
            }
        }
    }
}
