package com.example.personalgymapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.R
import com.example.personalgymapp.components.AddActionFab
import com.example.personalgymapp.model.TrainingSession
import com.example.personalgymapp.model.WorkoutPlan

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null
                        )
                        Text(stringResource(R.string.training_sessions))
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.schedule_manage_sessions),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search with Autocomplete
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { 
                        searchQuery = it
                        showAutocomplete = true
                    },
                    label = { Text(stringResource(R.string.search_by_name)) },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = ""; showAutocomplete = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true
                )

                if (showAutocomplete && suggestions.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 64.dp),
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

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(statuses) { status ->
                    FilterChip(
                        selected = selectedStatus == status,
                        onClick = { selectedStatus = status },
                        label = { Text(status) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredSessions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
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
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = session.clientName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
            
            if (workoutPlanName != null) {
                Text(
                    text = workoutPlanName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            StatusBadge(status = session.status)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${session.date} | ${session.time}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "${session.sessionType} (${session.durationMinutes} min)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val statusText = when(status) {
        "Scheduled" -> stringResource(R.string.status_scheduled)
        "Completed" -> stringResource(R.string.status_completed)
        "Cancelled" -> stringResource(R.string.status_cancelled)
        else -> status
    }

    val containerColor = when (status) {
        "Completed" -> MaterialTheme.colorScheme.primaryContainer
        "Cancelled" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
    val contentColor = when (status) {
        "Completed" -> MaterialTheme.colorScheme.onPrimaryContainer
        "Cancelled" -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = statusText,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall
        )
    }
}
