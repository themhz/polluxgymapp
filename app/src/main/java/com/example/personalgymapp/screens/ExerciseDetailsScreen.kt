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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
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
                title = { Text(exercise?.name ?: "Exercise Details") },
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

                ExerciseDetailItem(label = "Focus Area", value = exercise.focusArea)
                ExerciseDetailItem(label = "Training Type", value = exercise.trainingType)
                ExerciseDetailItem(label = "Muscle Group", value = exercise.muscleGroup)
                ExerciseDetailItem(label = "Equipment", value = exercise.equipment)
                ExerciseDetailItem(label = "Difficulty", value = exercise.difficulty)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Instructions",
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
