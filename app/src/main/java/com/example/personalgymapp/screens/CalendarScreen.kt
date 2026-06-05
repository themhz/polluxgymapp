package com.example.personalgymapp.screens

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.personalgymapp.R
import com.example.personalgymapp.components.AddActionFab
import com.example.personalgymapp.model.TrainingSession
import com.example.personalgymapp.model.WorkoutPlan
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    trainingSessions: List<TrainingSession>,
    workoutPlans: List<WorkoutPlan>,
    onTrainingSessionClick: (Int) -> Unit,
    onAddTrainingSessionClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val statusAll = stringResource(R.string.status_all)
    val statusScheduled = stringResource(R.string.status_scheduled)
    val statusCompleted = stringResource(R.string.status_completed)
    val statusCancelled = stringResource(R.string.status_cancelled)

    var selectedStatus by remember { mutableStateOf(statusAll) }
    val statuses = listOf(statusAll, statusScheduled, statusCompleted, statusCancelled)
    
    var searchQuery by remember { mutableStateOf("") }
    var showAutocomplete by remember { mutableStateOf(false) }

    val filteredSessions = trainingSessions.filter { session ->
        val planName = workoutPlans.find { it.id == session.workoutPlanId }?.name ?: ""
        val matchesStatus = selectedStatus == statusAll || session.status == when(selectedStatus) {
            statusScheduled -> "Scheduled"
            statusCompleted -> "Completed"
            statusCancelled -> "Cancelled"
            else -> session.status
        }
        val matchesSearch = session.clientName.contains(searchQuery, ignoreCase = true) || 
                            planName.contains(searchQuery, ignoreCase = true)
        
        matchesStatus && matchesSearch
    }

    // Autocomplete suggestions (Client names and Plan names)
    val suggestions = remember(searchQuery) {
        if (searchQuery.length < 2) emptyList()
        else {
            val clients = trainingSessions.map { it.clientName }
            val plans = workoutPlans.map { it.name }
            (clients + plans).distinct().filter { it.contains(searchQuery, ignoreCase = true) }.take(5)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.training_sessions),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
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
                label = stringResource(R.string.add_session),
                onClick = onAddTrainingSessionClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Search and Status Filter Header
            Surface(
                color = MaterialTheme.colorScheme.background,
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Search with Autocomplete
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { 
                                searchQuery = it
                                showAutocomplete = true
                            },
                            placeholder = { Text(stringResource(R.string.search_by_name)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = ""; showAutocomplete = false }) {
                                        Icon(Icons.Default.Close, contentDescription = "Clear")
                                    }
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )

                        if (showAutocomplete && suggestions.isNotEmpty()) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 60.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column {
                                    suggestions.forEach { suggestion ->
                                        ListItem(
                                            headlineContent = { Text(suggestion) },
                                            modifier = Modifier.clickable {
                                                searchQuery = suggestion
                                                showAutocomplete = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(statuses) { status ->
                            FilterChip(
                                selected = selectedStatus == status,
                                onClick = { selectedStatus = status },
                                label = { Text(status) },
                                shape = RoundedCornerShape(16.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }
            }

            if (filteredSessions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CalendarMonth, 
                            contentDescription = null, 
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outlineVariant
                        )
                        Spacer(Modifier.height(16.dp))
                        val message = if (selectedStatus == statusAll && searchQuery.isEmpty()) 
                            stringResource(R.string.no_sessions_yet) 
                        else if (searchQuery.isNotEmpty())
                            "No sessions found for \"$searchQuery\""
                        else 
                            stringResource(R.string.no_sessions_filtered, selectedStatus)
                        Text(
                            text = message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.tertiary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredSessions) { session ->
                        val planName = workoutPlans.find { it.id == session.workoutPlanId }?.name
                        SessionCard(
                            session = session,
                            workoutPlanName = planName,
                            onClick = { onTrainingSessionClick(session.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SessionCard(session: TrainingSession, workoutPlanName: String?, onClick: () -> Unit) {
    val context = LocalContext.current
    val addToCalendarText = stringResource(R.string.add_to_google_calendar)
    
    // Countdown and dynamic status state
    var remainingTimeMillis by remember { mutableStateOf(0L) }
    var dynamicStatus by remember { mutableStateOf(session.status) }

    val sessionStartTime = remember(session.date, session.time) {
        val calendar = Calendar.getInstance()
        val dateParts = session.date.split("-")
        if (dateParts.size == 3) {
            calendar.set(Calendar.YEAR, dateParts[0].toInt())
            calendar.set(Calendar.MONTH, dateParts[1].toInt() - 1)
            calendar.set(Calendar.DAY_OF_MONTH, dateParts[2].toInt())
        }
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        try {
            val timeDate = timeFormat.parse(session.time)
            if (timeDate != null) {
                val timeCal = Calendar.getInstance()
                timeCal.time = timeDate
                calendar.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                calendar.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                calendar.set(Calendar.SECOND, 0)
            }
        } catch (e: Exception) {}
        calendar.timeInMillis
    }

    LaunchedEffect(session.status, sessionStartTime) {
        if (session.status == "Scheduled") {
            while (true) {
                val now = System.currentTimeMillis()
                remainingTimeMillis = sessionStartTime - now
                
                // Update dynamic status based on time
                val today = Calendar.getInstance()
                val sessionCal = Calendar.getInstance().apply { timeInMillis = sessionStartTime }
                val isToday = today.get(Calendar.YEAR) == sessionCal.get(Calendar.YEAR) &&
                             today.get(Calendar.DAY_OF_YEAR) == sessionCal.get(Calendar.DAY_OF_YEAR)

                dynamicStatus = when {
                    remainingTimeMillis <= 0 -> "Missed"
                    isToday -> "Today Upcoming"
                    else -> "Scheduled"
                }

                if (remainingTimeMillis <= 0) {
                    remainingTimeMillis = 0
                    break
                }
                delay(1000L)
            }
        } else {
            dynamicStatus = session.status
        }
    }

    val cardColor = when(dynamicStatus) {
        "Completed" -> MaterialTheme.colorScheme.surface
        "Cancelled" -> MaterialTheme.colorScheme.surface
        "Missed" -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .height(IntrinsicSize.Min)
        ) {
            // Left Time Column
            Column(
                modifier = Modifier
                    .width(70.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                val timeParts = session.time.split(" ")
                Text(
                    text = if (timeParts.isNotEmpty()) timeParts[0] else "",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = if (dynamicStatus == "Missed") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                if (timeParts.size > 1) {
                    Text(
                        text = timeParts[1],
                        style = MaterialTheme.typography.labelSmall,
                        color = if (dynamicStatus == "Missed") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                    )
                }
                Spacer(Modifier.height(8.dp))
                val dateParts = session.date.split("-")
                if (dateParts.size == 3) {
                    Text(
                        text = "${dateParts[2]}/${dateParts[1]}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Divider
            VerticalDivider(
                modifier = Modifier.padding(horizontal = 12.dp),
                thickness = 1.dp,
                color = if (dynamicStatus == "Missed") MaterialTheme.colorScheme.error.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outlineVariant
            )

            // Middle Content Column
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = session.clientName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (dynamicStatus == "Missed") MaterialTheme.colorScheme.error else Color.Unspecified
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.Assignment,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = if (dynamicStatus == "Missed") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = workoutPlanName ?: "No Plan",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (dynamicStatus == "Missed") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(status = dynamicStatus)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "${session.durationMinutes}m • ${session.sessionType}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (dynamicStatus == "Missed") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary
                    )
                }

                if ((dynamicStatus == "Scheduled" || dynamicStatus == "Today Upcoming") && remainingTimeMillis > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val hours = remainingTimeMillis / (1000 * 60 * 60)
                    val minutes = (remainingTimeMillis / (1000 * 60)) % 60
                    val seconds = (remainingTimeMillis / 1000) % 60
                    
                    Surface(
                        color = if (dynamicStatus == "Today Upcoming") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Timer, 
                                contentDescription = null, 
                                modifier = Modifier.size(12.dp), 
                                tint = if (dynamicStatus == "Today Upcoming") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = String.format(Locale.getDefault(), "%02dh %02dm %02ds", hours, minutes, seconds),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                color = if (dynamicStatus == "Today Upcoming") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Right Action Column
            if (session.status == "Scheduled") {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = {
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = sessionStartTime

                            val intent = Intent(Intent.ACTION_INSERT)
                                .setData(CalendarContract.Events.CONTENT_URI)
                                .putExtra(CalendarContract.Events.TITLE, "Training with ${session.clientName}")
                                .putExtra(CalendarContract.Events.DESCRIPTION, "Training session: ${session.sessionType}\n${session.notes}")
                                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.timeInMillis)
                                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.timeInMillis + (session.durationMinutes * 60 * 1000))
                                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Gym")

                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .background(if (dynamicStatus == "Missed") MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = addToCalendarText,
                            tint = if (dynamicStatus == "Missed") MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val statusText = when(status) {
        "Scheduled" -> stringResource(R.string.status_scheduled)
        "Completed" -> stringResource(R.string.status_completed)
        "Cancelled" -> stringResource(R.string.status_cancelled)
        "Today Upcoming" -> "Today Upcoming"
        "Missed" -> "Expired/Missed"
        else -> status
    }

    val containerColor = when (status) {
        "Completed" -> Color(0xFFE8F5E9)
        "Cancelled" -> Color(0xFFFFEBEE)
        "Today Upcoming" -> Color(0xFFE3F2FD)
        "Missed" -> Color(0xFFFFEBEE)
        else -> Color(0xFFF5F5F5)
    }
    val contentColor = when (status) {
        "Completed" -> Color(0xFF2E7D32)
        "Cancelled" -> Color(0xFFC62828)
        "Today Upcoming" -> Color(0xFF1565C0)
        "Missed" -> Color(0xFFC62828)
        else -> Color(0xFF616161)
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = statusText,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
        )
    }
}
