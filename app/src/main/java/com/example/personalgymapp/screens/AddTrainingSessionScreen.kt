package com.example.personalgymapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.database.entity.ClientEntity
import com.example.personalgymapp.model.TrainingSession
import com.example.personalgymapp.model.WorkoutPlan
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTrainingSessionScreen(
    clients: List<ClientEntity>,
    workoutPlans: List<WorkoutPlan>,
    onSaveTrainingSession: (TrainingSession) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedClient by remember { mutableStateOf<ClientEntity?>(null) }
    var selectedWorkoutPlan by remember { mutableStateOf<WorkoutPlan?>(null) }
    var date by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var time by remember { mutableStateOf("10:00 AM") }
    var duration by remember { mutableStateOf("60") }
    var sessionType by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Scheduled") }
    var notes by remember { mutableStateOf("") }

    var clientError by remember { mutableStateOf<String?>(null) }
    var timeError by remember { mutableStateOf<String?>(null) }
    var durationError by remember { mutableStateOf<String?>(null) }
    var sessionTypeError by remember { mutableStateOf<String?>(null) }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(initialHour = 10, initialMinute = 0)
    var showTimePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                    cal.set(Calendar.MINUTE, timePickerState.minute)
                    time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time)
                    showTimePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    val statuses = listOf("Scheduled", "Completed", "Cancelled")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Session") },
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
            // Client Selection
            var expandedClient by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedClient,
                onExpandedChange = { expandedClient = !expandedClient }
            ) {
                OutlinedTextField(
                    value = selectedClient?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Client") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedClient) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    isError = clientError != null,
                    supportingText = { clientError?.let { Text(it) } }
                )
                ExposedDropdownMenu(
                    expanded = expandedClient,
                    onDismissRequest = { expandedClient = false }
                ) {
                    clients.forEach { client ->
                        DropdownMenuItem(
                            text = { Text(client.name) },
                            onClick = {
                                selectedClient = client
                                expandedClient = false
                                clientError = null
                            }
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { },
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = time,
                    onValueChange = { },
                    label = { Text("Time") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.Refresh, contentDescription = "Select Time")
                    },
                    isError = timeError != null,
                    supportingText = { timeError?.let { Text(it) } }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showTimePicker = true }
                )
            }

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it; durationError = null },
                label = { Text("Duration (minutes)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = durationError != null,
                supportingText = { durationError?.let { Text(it) } }
            )

            OutlinedTextField(
                value = sessionType,
                onValueChange = { sessionType = it; sessionTypeError = null },
                label = { Text("Session Type (e.g. Strength)") },
                modifier = Modifier.fillMaxWidth(),
                isError = sessionTypeError != null,
                supportingText = { sessionTypeError?.let { Text(it) } }
            )

            // Workout Plan Selection
            var expandedPlan by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedPlan,
                onExpandedChange = { expandedPlan = !expandedPlan }
            ) {
                OutlinedTextField(
                    value = selectedWorkoutPlan?.name ?: "No workout plan",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Workout Plan (Optional)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPlan) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedPlan,
                    onDismissRequest = { expandedPlan = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("No workout plan") },
                        onClick = {
                            selectedWorkoutPlan = null
                            expandedPlan = false
                        }
                    )
                    workoutPlans.forEach { plan ->
                        DropdownMenuItem(
                            text = { Text(plan.name) },
                            onClick = {
                                selectedWorkoutPlan = plan
                                expandedPlan = false
                            }
                        )
                    }
                }
            }

            Text("Status", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statuses.forEach { s ->
                    FilterChip(
                        selected = status == s,
                        onClick = { status = s },
                        label = { Text(s) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    var isValid = true
                    if (selectedClient == null) {
                        clientError = "Please select a client"
                        isValid = false
                    }
                    if (time.isBlank()) {
                        timeError = "Time is required"
                        isValid = false
                    }
                    val durationInt = duration.toIntOrNull()
                    if (duration.isBlank() || durationInt == null || durationInt <= 0) {
                        durationError = "Enter a valid duration"
                        isValid = false
                    }
                    if (sessionType.isBlank()) {
                        sessionTypeError = "Session type is required"
                        isValid = false
                    }

                    if (isValid) {
                        onSaveTrainingSession(
                            TrainingSession(
                                id = 0, // Assigned in Navigation
                                clientId = selectedClient!!.id,
                                clientName = selectedClient!!.name,
                                date = date,
                                time = time,
                                durationMinutes = durationInt ?: 0,
                                sessionType = sessionType,
                                status = status,
                                notes = notes,
                                workoutPlanId = selectedWorkoutPlan?.id
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Session")
            }
        }
    }
}
