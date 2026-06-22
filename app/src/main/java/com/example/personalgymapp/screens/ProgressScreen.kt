package com.example.personalgymapp.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.personalgymapp.R
import com.example.personalgymapp.components.FullScreenMapDialog
import com.example.personalgymapp.database.entity.ClientEntity
import com.example.personalgymapp.model.GPSPoint
import com.example.personalgymapp.model.SessionExerciseResult
import com.example.personalgymapp.model.SessionSetResult
import com.example.personalgymapp.model.TrainingSession
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    clients: List<ClientEntity>,
    sessions: List<TrainingSession>,
    results: List<SessionExerciseResult>,
    onBackClick: () -> Unit
) {
    var selectedClient by remember { mutableStateOf<ClientEntity?>(null) }
    var expandedClientDropdown by remember { mutableStateOf(false) }
    var fullScreenMapPoints by remember { mutableStateOf<List<GPSPoint>?>(null) }

    val clientExerciseProgress = remember(selectedClient, sessions, results) {
        if (selectedClient == null) emptyMap<String, List<SessionExerciseResult>>()
        else {
            val clientSessionIds = sessions.filter { it.clientId == selectedClient!!.id }.map { it.id }.toSet()
            results.filter { it.trainingSessionId in clientSessionIds }
                .groupBy { it.exerciseName }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null)
                        Text(stringResource(R.string.progress_title))
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
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Client Selection Dropdown
            Text(
                text = stringResource(R.string.select_client_progress),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedClient?.name ?: stringResource(R.string.select_client),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { expandedClientDropdown = !expandedClientDropdown }) {
                            Icon(
                                imageVector = if (expandedClientDropdown) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                    },
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
                DropdownMenu(
                    expanded = expandedClientDropdown,
                    onDismissRequest = { expandedClientDropdown = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    clients.forEach { client ->
                        DropdownMenuItem(
                            text = { Text(client.name) },
                            onClick = {
                                selectedClient = client
                                expandedClientDropdown = false
                            }
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { expandedClientDropdown = !expandedClientDropdown }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedClient == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.select_client_to_see_results), color = MaterialTheme.colorScheme.tertiary)
                }
            } else if (clientExerciseProgress.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_results_found), color = MaterialTheme.colorScheme.tertiary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(clientExerciseProgress.keys.toList()) { exerciseName ->
                        val exerciseResults = clientExerciseProgress[exerciseName] ?: emptyList()
                        ExerciseProgressCard(
                            exerciseName = exerciseName,
                            results = exerciseResults,
                            sessions = sessions,
                            onMapClick = { fullScreenMapPoints = it }
                        )
                    }
                }
            }
        }
    }

    if (fullScreenMapPoints != null) {
        FullScreenMapDialog(
            points = fullScreenMapPoints!!,
            onDismiss = { fullScreenMapPoints = null }
        )
    }
}

@Composable
fun ExerciseProgressCard(
    exerciseName: String,
    results: List<SessionExerciseResult>,
    sessions: List<TrainingSession>,
    onMapClick: (List<GPSPoint>) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = exerciseName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            
            // Calculate bests
            val allSets = results.flatMap { it.sets }
            val maxWeight = allSets.mapNotNull { it.weightKg }.maxOrNull()
            val maxReps = allSets.mapNotNull { it.reps }.maxOrNull()
            val maxDuration = allSets.mapNotNull { it.durationSeconds }.maxOrNull()

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                if (maxWeight != null) {
                    ProgressStat(label = stringResource(R.string.max_weight), value = "$maxWeight kg")
                }
                if (maxReps != null) {
                    ProgressStat(label = stringResource(R.string.max_reps), value = "$maxReps")
                }
                if (maxDuration != null) {
                    ProgressStat(label = stringResource(R.string.max_time), value = "${maxDuration}s")
                }
            }
            
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Line Chart
                    ExerciseProgressChart(results = results, sessions = sessions)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.history_last_3),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    
                    results.takeLast(3).reversed().forEach { result ->
                        val sessionDate = sessions.find { it.id == result.trainingSessionId }?.date ?: "Unknown"
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = sessionDate, style = MaterialTheme.typography.bodySmall)
                                val bestSet = result.sets.maxByOrNull { (it.weightKg ?: 0.0) * (it.reps ?: 1).toDouble() }
                                if (bestSet != null) {
                                    val info = if (bestSet.weightKg != null) "${bestSet.weightKg}kg x ${bestSet.reps}" else "${bestSet.durationSeconds}s"
                                    Text(text = info, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                            }
                            
                            if (result.gpsPath != null && result.gpsPath.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                ) {
                                    val pathPoints = result.gpsPath.map { GeoPoint(it.latitude, it.longitude) }
                                    AndroidView(
                                        factory = { context ->
                                            MapView(context).apply {
                                                setTileSource(TileSourceFactory.MAPNIK)
                                                setMultiTouchControls(false)
                                                if (pathPoints.isNotEmpty()) {
                                                    controller.setZoom(16.0)
                                                    controller.setCenter(pathPoints.first())
                                                    val polyline = Polyline(this)
                                                    polyline.setPoints(pathPoints)
                                                    polyline.outlinePaint.color = android.graphics.Color.BLUE
                                                    polyline.outlinePaint.strokeWidth = 6f
                                                    overlays.add(polyline)
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxSize(),
                                        update = { it.invalidate() }
                                    )
                                    // Clickable overlay to enlarge
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable { onMapClick(result.gpsPath) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseProgressChart(
    results: List<SessionExerciseResult>,
    sessions: List<TrainingSession>
) {
    // Determine the metric to chart (Weight or Duration)
    val chartData = results.mapNotNull { result ->
        val session = sessions.find { it.id == result.trainingSessionId }
        val date = session?.date ?: ""
        
        // Find "best" value for this session
        val bestSet = result.sets.maxByOrNull { (it.weightKg ?: 0.0) * (it.reps ?: 1).toDouble() }
        val value = bestSet?.weightKg ?: bestSet?.durationSeconds?.toDouble()
        
        if (value != null) date to value else null
    }

    if (chartData.size < 2) {
        return // Not enough data for a line chart
    }

    val values = chartData.map { it.second }
    val minVal = values.minOrNull() ?: 0.0
    val maxVal = values.maxOrNull() ?: 1.0
    val range = (maxVal - minVal).coerceAtLeast(1.0)
    
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Column(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            val width = size.width
            val height = size.height
            val spacing = width / (chartData.size - 1)
            
            val path = Path()
            chartData.forEachIndexed { index, data ->
                val x = index * spacing
                val y = height - ((data.second - minVal) / range * height).toFloat()
                
                if (index == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
                
                // Draw point
                drawCircle(
                    color = primaryColor,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }
            
            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        
        // Date labels (First and Last)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = chartData.first().first, style = MaterialTheme.typography.bodySmall, fontSize = 10.sp)
            Text(text = chartData.last().first, style = MaterialTheme.typography.bodySmall, fontSize = 10.sp)
        }
    }
}

@Composable
fun ProgressStat(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold, color = Color(0xFF008080))
    }
}
