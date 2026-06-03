package com.example.personalgymapp.screens

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.personalgymapp.R
import com.example.personalgymapp.model.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailsScreen(
    exercise: Exercise?,
    onEditClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(exercise?.name ?: stringResource(R.string.exercise_library)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (exercise != null) {
                        IconButton(onClick = { onEditClick(exercise.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
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
        if (exercise == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(text = "Exercise not found!", color = MaterialTheme.colorScheme.error)
            }
        } else {
            val context = LocalContext.current

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
            val difficulties = mapOf(
                "Beginner" to stringResource(R.string.diff_beginner),
                "Intermediate" to stringResource(R.string.diff_intermediate),
                "Advanced" to stringResource(R.string.diff_advanced)
            )

            val imageModel = remember(exercise.imageResName, exercise.imageUri) {
                if (exercise.imageUri != null) {
                    exercise.imageUri
                } else if (exercise.imageResName != null) {
                    context.resources.getIdentifier(exercise.imageResName, "drawable", context.packageName).let {
                        if (it != 0) it else null
                    }
                } else null
            }
            
            val videoResId = remember(exercise.videoResName) {
                if (exercise.videoResName != null) {
                    context.resources.getIdentifier(exercise.videoResName, "raw", context.packageName)
                } else 0
            }

            val videoUri = remember(exercise.videoUri) {
                if (exercise.videoUri != null) {
                    Uri.parse(exercise.videoUri)
                } else null
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (videoUri != null || videoResId != 0) {
                    Text(
                        text = "Exercise Video",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color.Black)
                    ) {
                        AndroidView(
                            factory = { ctx ->
                                VideoView(ctx).apply {
                                    val uri = videoUri ?: Uri.parse("android.resource://${ctx.packageName}/$videoResId")
                                    setVideoURI(uri)
                                    val controller = MediaController(ctx)
                                    controller.setAnchorView(this)
                                    setMediaController(controller)
                                    setOnPreparedListener { 
                                        it.isLooping = true
                                        it.start()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                } else if (imageModel != null) {
                    AsyncImage(
                        model = imageModel,
                        contentDescription = exercise.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                ExerciseDetailItem(label = stringResource(R.string.focus_area), value = focusAreas[exercise.focusArea] ?: exercise.focusArea)
                ExerciseDetailItem(label = stringResource(R.string.training_type), value = trainingTypes[exercise.trainingType] ?: exercise.trainingType)
                ExerciseDetailItem(label = stringResource(R.string.muscle_group), value = muscleGroups[exercise.muscleGroup] ?: exercise.muscleGroup)
                ExerciseDetailItem(label = stringResource(R.string.equipment), value = exercise.equipment)
                ExerciseDetailItem(label = stringResource(R.string.difficulty), value = difficulties[exercise.difficulty] ?: exercise.difficulty)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(R.string.instructions),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = exercise.instructions,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

@Composable
fun ExerciseDetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
