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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.R
import com.example.personalgymapp.components.FieldExplanation
import com.example.personalgymapp.model.TrainingSession
import com.example.personalgymapp.model.WorkoutPlan
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTrainingSessionScreen(
    session: TrainingSession?,
    workoutPlans: List<WorkoutPlan>,
    onSaveTrainingSession: (TrainingSession) -> Unit,
    onBackClick: () -> Unit
) {
    if (session == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.session_not_found))
        }
        return
    }

    var date by remember { mutableStateOf(session.date) }
    var time by remember { mutableStateOf(session.time) }
    var duration by remember { mutableStateOf(session.durationMinutes.toString()) }
    var sessionType by remember { mutableStateOf(session.sessionType) }
    var status by remember { mutableStateOf(session.status) }
    var notes by remember { mutableStateOf(session.notes) }
    var selectedWorkoutPlanId by remember { mutableStateOf(session.workoutPlanId) }

    var timeError by remember { mutableStateOf<String?>(null) }
    var durationError by remember { mutableStateOf<String?>(null) }
    var sessionTypeError by remember { mutableStateOf<String?>(null) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = try { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(session.date)?.time } catch (e: Exception) { null }
    )
    var showDatePicker by remember { mutableStateOf(false) }

    // Parsing time to set initial picker state
    val initialCal = Calendar.getInstance().apply {
        try {
            val parsedDate = SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(session.time)
            if (parsedDate != null) {
                this.time = parsedDate
            }
        } catch (e: Exception) {}
    }
    val timePickerState = rememberTimePickerState(
        initialHour = initialCal.get(Calendar.HOUR_OF_DAY),
        initialMinute = initialCal.get(Calendar.MINUTE)
    )
    var showTimePicker by remember { mutableStateOf(false) }

    val okText = stringResource(R.string.ok)
    val cancelText = stringResource(R.string.cancel)
    
    val timeRequiredError = stringResource(R.string.error_time_required)
    val enterDurationError = stringResource(R.string.error_enter_duration)
    val sessionTypeRequiredError = stringResource(R.string.error_session_type_required)

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
                    Text(okText)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(cancelText)
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
                    Text(okText)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(cancelText)
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    val statuses = mapOf(
        "Scheduled" to stringResource(R.string.status_scheduled),
        "Completed" to stringResource(R.string.status_completed),
        "Cancelled" to stringResource(R.string.status_cancelled)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_session)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = cancelText)
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
            // Client Name (Read-only in edit)
            OutlinedTextField(
                value = session.clientName,
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(R.string.client)) },
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { },
                    label = { Text(stringResource(R.string.date)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FieldExplanation(explanation = stringResource(R.string.ts_date_desc))
                            Icon(Icons.Default.DateRange, contentDescription = null)
                        }
                    }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(end = 48.dp)
                        .clickable { showDatePicker = true }
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = time,
                    onValueChange = { },
                    label = { Text(stringResource(R.string.time)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FieldExplanation(explanation = stringResource(R.string.ts_time_desc))
                            Icon(Icons.Default.Refresh, contentDescription = null)
                        }
                    },
                    isError = timeError != null,
                    supportingText = { timeError?.let { Text(it) } }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(end = 48.dp)
                        .clickable { showTimePicker = true }
                )
            }

            OutlinedTextField(
                value = duration,
                onValueChange = { duration = it; durationError = null },
                label = { Text(stringResource(R.string.duration)) },
                placeholder = { Text(stringResource(R.string.duration_mins, 60)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = durationError != null,
                supportingText = { durationError?.let { Text(it) } },
                trailingIcon = {
                    FieldExplanation(explanation = stringResource(R.string.ts_duration_desc))
                }
            )

            OutlinedTextField(
                value = sessionType,
                onValueChange = { sessionType = it; sessionTypeError = null },
                label = { Text(stringResource(R.string.session_type)) },
                placeholder = { Text(stringResource(R.string.session_type_hint)) },
                modifier = Modifier.fillMaxWidth(),
                isError = sessionTypeError != null,
                supportingText = { sessionTypeError?.let { Text(it) } },
                trailingIcon = {
                    FieldExplanation(explanation = stringResource(R.string.ts_type_desc))
                }
            )

            // Workout Plan Selection
            var expandedPlan by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expandedPlan,
                onExpandedChange = { expandedPlan = !expandedPlan }
            ) {
                val selectedPlanName = workoutPlans.find { it.id == selectedWorkoutPlanId }?.name ?: stringResource(R.string.no_workout_plan)
                OutlinedTextField(
                    value = selectedPlanName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.workout_plan_optional)) },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            FieldExplanation(explanation = stringResource(R.string.ts_plan_desc))
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPlan)
                        }
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedPlan,
                    onDismissRequest = { expandedPlan = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.no_workout_plan)) },
                        onClick = {
                            selectedWorkoutPlanId = null
                            expandedPlan = false
                        }
                    )
                    workoutPlans.forEach { plan ->
                        DropdownMenuItem(
                            text = { Text(plan.name) },
                            onClick = {
                                selectedWorkoutPlanId = plan.id
                                expandedPlan = false
                            }
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.status), style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.width(8.dp))
                FieldExplanation(explanation = stringResource(R.string.ts_status_desc))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statuses.forEach { (key, label) ->
                    FilterChip(
                        selected = status == key,
                        onClick = { status = key },
                        label = { Text(label) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.notes_optional)) },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                maxLines = 3,
                trailingIcon = {
                    FieldExplanation(explanation = stringResource(R.string.ts_notes_desc))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    var isValid = true
                    if (time.isBlank()) {
                        timeError = timeRequiredError
                        isValid = false
                    }
                    val durationInt = duration.toIntOrNull()
                    if (duration.isBlank() || durationInt == null || durationInt <= 0) {
                        durationError = enterDurationError
                        isValid = false
                    }
                    if (sessionType.isBlank()) {
                        sessionTypeError = sessionTypeRequiredError
                        isValid = false
                    }

                    if (isValid) {
                        onSaveTrainingSession(
                            session.copy(
                                date = date,
                                time = time,
                                durationMinutes = durationInt ?: 0,
                                sessionType = sessionType,
                                status = status,
                                notes = notes,
                                workoutPlanId = selectedWorkoutPlanId
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_changes))
            }
        }
    }
}
