package com.example.personalgymapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.database.entity.ClientEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClientScreen(
    client: ClientEntity?,
    onSaveClient: (ClientEntity) -> Unit,
    onBackClick: () -> Unit,
) {
    if (client == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Client not found")
        }
        return
    }

    var name by remember { mutableStateOf(client.name) }
    var goal by remember { mutableStateOf(client.goal) }
    var birthDate by remember { mutableStateOf(client.birthDate) }
    var phone by remember { mutableStateOf(client.phone) }
    var email by remember { mutableStateOf(client.email) }
    var nextSession by remember { mutableStateOf(client.nextSession) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var goalError by remember { mutableStateOf<String?>(null) }
    var birthDateError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val birthDatePickerState = rememberDatePickerState()
    var showBirthDatePicker by remember { mutableStateOf(false) }
    
    val sessionDatePickerState = rememberDatePickerState()
    var showSessionDatePicker by remember { mutableStateOf(false) }

    if (showBirthDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showBirthDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    birthDatePickerState.selectedDateMillis?.let {
                        birthDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                    }
                    showBirthDatePicker = false
                    birthDateError = null
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBirthDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = birthDatePickerState)
        }
    }

    if (showSessionDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showSessionDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    sessionDatePickerState.selectedDateMillis?.let {
                        nextSession = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                    }
                    showSessionDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSessionDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = sessionDatePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Client") },
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
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it) } }
            )

            OutlinedTextField(
                value = goal,
                onValueChange = { goal = it; goalError = null },
                label = { Text("Goal") },
                modifier = Modifier.fillMaxWidth(),
                isError = goalError != null,
                supportingText = { goalError?.let { Text(it) } }
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = birthDate,
                    onValueChange = { },
                    label = { Text("Birth Date") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    isError = birthDateError != null,
                    supportingText = { birthDateError?.let { Text(it) } },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Birth Date")
                    }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showBirthDatePicker = true }
                )
            }

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null,
                supportingText = { emailError?.let { Text(it) } }
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nextSession,
                    onValueChange = { },
                    label = { Text("Next Session") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showSessionDatePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    var isValid = true
                    if (name.isBlank()) {
                        nameError = "Name is required"
                        isValid = false
                    }
                    if (goal.isBlank()) {
                        goalError = "Goal is required"
                        isValid = false
                    }
                    if (birthDate.isBlank()) {
                        birthDateError = "Birth date is required"
                        isValid = false
                    }
                    if (email.isBlank()) {
                        emailError = "Email is required"
                        isValid = false
                    }

                    if (isValid) {
                        onSaveClient(
                            client.copy(
                                name = name,
                                goal = goal,
                                birthDate = birthDate,
                                phone = phone,
                                email = email,
                                nextSession = nextSession
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Client")
            }
        }
    }
}
