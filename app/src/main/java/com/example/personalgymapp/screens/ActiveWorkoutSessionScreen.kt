package com.example.personalgymapp.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.personalgymapp.model.*
import com.google.android.gms.location.*
import kotlinx.coroutines.delay
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
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

    val context = LocalContext.current
    var isStarted by remember { mutableStateOf(false) }
    var currentExerciseIndex by remember { mutableStateOf(0) }
    
    // State for all exercises: Map<ExerciseIndex, List<SetResults>>
    val sessionProgress = remember { mutableStateMapOf<Int, MutableList<SessionSetResult>>() }
    val exerciseGpsPaths = remember { mutableStateMapOf<Int, List<GPSPoint>>() }
    
    // Initialize session progress with empty lists
    LaunchedEffect(workoutPlan) {
        workoutPlan.exercises.forEachIndexed { index, _ ->
            if (!sessionProgress.containsKey(index)) {
                sessionProgress[index] = mutableListOf()
            }
        }
    }

    val currentExercise = workoutPlan.exercises[currentExerciseIndex]
    val isOutdoorRun = currentExercise.isGpsEnabled

    val currentExerciseSets = sessionProgress[currentExerciseIndex] ?: mutableListOf()
    val isExerciseFinished = currentExerciseSets.size >= currentExercise.sets

    // GPS Tracking State
    var isGpsTrackingActive by remember { mutableStateOf(false) }
    val currentPath = remember { mutableStateListOf<GPSPoint>() }
    var currentSpeed by remember { mutableStateOf(0f) } // m/s
    var currentPace by remember { mutableStateOf("0:00") }

    // Fused Location Provider
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val point = GPSPoint(location.latitude, location.longitude, System.currentTimeMillis())
                    currentPath.add(point)
                    currentSpeed = location.speed
                    
                    if (location.speed > 0.5f) { // Only calculate pace if moving
                        val paceSecondsPerKm = (1000 / location.speed).toInt()
                        val min = paceSecondsPerKm / 60
                        val sec = paceSecondsPerKm % 60
                        currentPace = String.format(Locale.getDefault(), "%d:%02d", min, sec)
                    } else {
                        currentPace = "0:00"
                    }
                }
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            isGpsTrackingActive = true
        }
    }

    // Effect to start/stop location updates
    LaunchedEffect(isGpsTrackingActive) {
        if (isGpsTrackingActive) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
                .setMinUpdateIntervalMillis(2000L)
                .build()
            
            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                isGpsTrackingActive = false
            }
        } else {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    // Stop tracking when moving away from exercise or finishing
    LaunchedEffect(currentExerciseIndex) {
        if (isGpsTrackingActive) {
            // Save path before clearing if it was an outdoor run
            if (isOutdoorRun && currentPath.isNotEmpty()) {
                exerciseGpsPaths[currentExerciseIndex - 1] = currentPath.toList()
            }
            isGpsTrackingActive = false
            currentPath.clear()
        }
    }

    // Input state
    var repsInput by remember { mutableStateOf(currentExercise.reps.toString()) }
    var weightInput by remember { mutableStateOf("") }
    var notesInput by remember { mutableStateOf("") }

    // Timer and Mode state
    var isResting by remember { mutableStateOf(false) }
    var timerSeconds by remember { mutableStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Sound Generator for timer completion
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }

    // Helper to add a set result
    val completeSet = {
        val reps = if (currentExercise.exerciseType == "REPS") repsInput.toIntOrNull() else null
        val weight = weightInput.toDoubleOrNull()
        val duration = if (currentExercise.exerciseType == "TIME") {
            if (currentExercise.timerType == "COUNTUP") timerSeconds else (currentExercise.targetDurationSeconds ?: 0) - timerSeconds
        } else null

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
        
        // If it was an outdoor run and this was the last set/round, save the path
        if (isOutdoorRun && updatedList.size >= currentExercise.sets) {
            exerciseGpsPaths[currentExerciseIndex] = currentPath.toList()
            isGpsTrackingActive = false
        }

        // Handle rest or next set
        if (updatedList.size < currentExercise.sets && currentExercise.restSeconds > 0) {
            isResting = true
            timerSeconds = currentExercise.restSeconds
            isTimerRunning = true
        } else {
            isResting = false
            if (updatedList.size < currentExercise.sets && currentExercise.exerciseType == "TIME") {
                timerSeconds = if (currentExercise.timerType == "COUNTUP") 0 else (currentExercise.targetDurationSeconds ?: 0)
                isTimerRunning = true
            } else {
                isTimerRunning = false
            }
        }
    }

    // Timer Logic
    LaunchedEffect(isTimerRunning, isResting, currentExercise) {
        while (isTimerRunning) {
            val targetTime = if (isResting) 0 else {
                if (currentExercise.exerciseType == "TIME" && currentExercise.timerType == "COUNTUP")
                    currentExercise.targetDurationSeconds ?: 0
                else 0
            }

            if (timerSeconds == targetTime) break

            delay(1000L)
            
            if (isResting || (currentExercise.exerciseType == "TIME" && currentExercise.timerType == "COUNTDOWN")) {
                timerSeconds -= 1
            } else if (currentExercise.exerciseType == "TIME" && currentExercise.timerType == "COUNTUP") {
                timerSeconds += 1
            }
        }

        if (isTimerRunning) {
            val isFinished = if (isResting) {
                timerSeconds <= 0
            } else if (currentExercise.exerciseType == "TIME") {
                if (currentExercise.timerType == "COUNTUP") {
                    timerSeconds >= (currentExercise.targetDurationSeconds ?: 0)
                } else {
                    timerSeconds <= 0
                }
            } else false

            if (isFinished) {
                if (isResting) {
                    isResting = false
                    if (currentExercise.exerciseType == "TIME") {
                        timerSeconds = if (currentExercise.timerType == "COUNTUP") 0 else (currentExercise.targetDurationSeconds ?: 0)
                        isTimerRunning = true
                    } else {
                        isTimerRunning = false
                    }
                } else if (currentExercise.exerciseType == "TIME") {
                    // Play a beep sound when the exercise time finishes
                    toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 500)
                    completeSet()
                }
            }
        }
    }

    // Reset/Setup timer when moving to a new exercise
    LaunchedEffect(currentExerciseIndex) {
        isResting = false
        isTimerRunning = false
        val ex = workoutPlan.exercises[currentExerciseIndex]
        repsInput = ex.reps.toString()
        timerSeconds = if (ex.exerciseType == "TIME") {
            if (ex.timerType == "COUNTUP") 0 else (ex.targetDurationSeconds ?: 0)
        } else 0
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
                            exerciseGpsPaths.clear()
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
                        val setLabel = if (currentExercise.exerciseType == "REPS") "Set" else "Round"
                        Text(
                            text = if (isResting) "Next: $setLabel ${currentExerciseSets.size + 1}" else "$setLabel ${currentExerciseSets.size + 1}",
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
                    val setsLabel = if (currentExercise.exerciseType == "REPS") "sets" else "rounds"
                    Text(text = "Goal: ${currentExercise.sets} $setsLabel", fontWeight = FontWeight.Bold)
                    val goalText = if (currentExercise.exerciseType == "REPS") "${currentExercise.reps} reps" else "${currentExercise.targetDurationSeconds}s"
                    Text(text = "Target: $goalText | Rest: ${currentExercise.restSeconds}s")
                }
            }

            if (!isStarted) {
                Button(
                    onClick = { 
                        isStarted = true 
                        if (currentExercise.exerciseType == "TIME") {
                            timerSeconds = if (currentExercise.timerType == "COUNTUP") 0 else (currentExercise.targetDurationSeconds ?: 0)
                            isTimerRunning = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp)
                ) {
                    Text("Start the Session")
                }
            } else {
                // GPS Tracking UI
                if (isOutdoorRun && !isResting) {
                    GpsTrackingSection(
                        isTracking = isGpsTrackingActive,
                        currentPath = currentPath,
                        currentSpeed = currentSpeed,
                        currentPace = currentPace,
                        onStartTracking = {
                            val fineLocation = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                            if (fineLocation == PackageManager.PERMISSION_GRANTED) {
                                isGpsTrackingActive = true
                            } else {
                                locationPermissionLauncher.launch(
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                                )
                            }
                        },
                        onStopTracking = { isGpsTrackingActive = false }
                    )
                }

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
                        val recordedLabel = if (currentExercise.exerciseType == "REPS") "Recorded Sets:" else "Recorded Rounds:"
                        Text(recordedLabel, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                        currentExerciseSets.forEach { set ->
                            val resText = if (currentExercise.exerciseType == "TIME") {
                                "${set.durationSeconds ?: 0}s"
                            } else {
                                "${set.reps ?: 0} reps"
                            }
                            val weightText = if (set.weightKg != null && set.weightKg > 0) " @ ${set.weightKg}kg" else ""
                            val setLabel = if (currentExercise.exerciseType == "REPS") "Set" else "Round"
                            Text(text = "$setLabel ${set.setNumber}: $resText$weightText", style = MaterialTheme.typography.bodySmall)
                        }
                        
                        TextButton(
                            onClick = { sessionProgress[currentExerciseIndex] = mutableListOf() },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            val resetLabel = if (currentExercise.exerciseType == "REPS") "Reset Exercise Progress" else "Reset Round Progress"
                            Text(resetLabel)
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
                                        notes = "",
                                        gpsPath = exerciseGpsPaths[index]
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
                            val setLabel = if (currentExercise.exerciseType == "REPS") "Set" else "Round"
                            Text("Complete $setLabel ${currentExerciseSets.size + 1}")
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun GpsTrackingSection(
    isTracking: Boolean,
    currentPath: List<GPSPoint>,
    currentSpeed: Float,
    currentPace: String,
    onStartTracking: () -> Unit,
    onStopTracking: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.AutoMirrored.Filled.DirectionsRun, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text("Outdoor GPS Tracking", fontWeight = FontWeight.Bold)
                }
                
                Switch(
                    checked = isTracking,
                    onCheckedChange = { if (it) onStartTracking() else onStopTracking() }
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Pace", style = MaterialTheme.typography.labelSmall)
                    Text(currentPace, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    Text("min/km", style = MaterialTheme.typography.labelSmall)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Speed", style = MaterialTheme.typography.labelSmall)
                    Text(String.format(Locale.getDefault(), "%.1f", currentSpeed * 3.6f), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                    Text("km/h", style = MaterialTheme.typography.labelSmall)
                }
            }

            AnimatedVisibility(visible = isTracking || currentPath.isNotEmpty()) {
                val lastPos = if (currentPath.isNotEmpty()) {
                    GeoPoint(currentPath.last().latitude, currentPath.last().longitude)
                } else {
                    GeoPoint(0.0, 0.0)
                }
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AndroidView(
                        factory = { context ->
                            MapView(context).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                                controller.setZoom(17.0)
                                if (lastPos.latitude != 0.0) {
                                    controller.setCenter(lastPos)
                                }
                            }
                        },
                        update = { mapView ->
                            if (lastPos.latitude != 0.0) {
                                mapView.controller.animateTo(lastPos)
                            }
                            
                            if (currentPath.size > 1) {
                                mapView.overlays.clear()
                                val polyline = Polyline(mapView)
                                polyline.setPoints(currentPath.map { GeoPoint(it.latitude, it.longitude) })
                                polyline.outlinePaint.color = android.graphics.Color.BLUE // Set a default color
                                polyline.outlinePaint.strokeWidth = 10f
                                mapView.overlays.add(polyline)
                            }
                            mapView.invalidate()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
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
