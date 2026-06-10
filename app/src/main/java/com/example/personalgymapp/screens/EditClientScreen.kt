package com.example.personalgymapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.R
import com.example.personalgymapp.components.FieldExplanation
import com.example.personalgymapp.database.entity.ClientEntity
import com.example.personalgymapp.database.entity.SubscriptionPlanEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClientScreen(
    client: ClientEntity?,
    subscriptionPlans: List<SubscriptionPlanEntity>,
    onSaveClient: (ClientEntity) -> Unit,
    onDeleteClient: (ClientEntity) -> Unit,
    onBackClick: () -> Unit,
) {
    val noMatchingClientsText = stringResource(R.string.no_matching_clients)
    val deleteClientText = stringResource(R.string.delete_client)
    val deleteClientConfirmationText = stringResource(R.string.delete_client_confirmation)
    val deleteText = stringResource(R.string.delete)
    val cancelText = stringResource(R.string.cancel)
    val okText = stringResource(R.string.ok)
    val editClientText = stringResource(R.string.edit_client)
    val nameLabel = stringResource(R.string.client)
    val nextSessionLabel = stringResource(R.string.next_session).substringBefore(":")
    val saveClientText = stringResource(R.string.save_client)
    val backContentDescription = stringResource(R.string.status_cancelled).substringBefore(" ")
    val nameRequiredError = stringResource(R.string.error_name_required)

    if (client == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text(noMatchingClientsText)
        }
        return
    }

    var name by remember { mutableStateOf(client.name) }
    var goal by remember { mutableStateOf(client.goal) }
    var birthDate by remember { mutableStateOf<Date?>(client.birthDate) }
    var phone by remember { mutableStateOf(client.phone) }
    var email by remember { mutableStateOf(client.email) }
    var nextSession by remember { mutableStateOf(client.nextSession) }
    var selectedPlanId by remember { mutableStateOf(client.subscriptionPlanId) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var goalError by remember { mutableStateOf<String?>(null) }
    var birthDateError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val birthDatePickerState = rememberDatePickerState()
    var showBirthDatePicker by remember { mutableStateOf(false) }
    
    val sessionDatePickerState = rememberDatePickerState()
    var showSessionDatePicker by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var expandedPlans by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(deleteClientText) },
            text = { Text(deleteClientConfirmationText) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteClient(client)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(deleteText)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(cancelText)
                }
            }
        )
    }

    if (showBirthDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showBirthDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    birthDatePickerState.selectedDateMillis?.let {
                        birthDate = Date(it)
                    }
                    showBirthDatePicker = false
                    birthDateError = null
                }) {
                    Text(okText)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBirthDatePicker = false }) {
                    Text(cancelText)
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
                    Text(okText)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSessionDatePicker = false }) {
                    Text(cancelText)
                }
            }
        ) {
            DatePicker(state = sessionDatePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(editClientText) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = backContentDescription)
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = deleteClientText,
                            tint = MaterialTheme.colorScheme.error
                        )
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
                label = { Text(nameLabel) },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it) } },
                trailingIcon = {
                    FieldExplanation(explanation = stringResource(R.string.client_name_desc))
                }
            )

            OutlinedTextField(
                value = goal,
                onValueChange = { goal = it; goalError = null },
                label = { Text(stringResource(R.string.client_goal)) },
                modifier = Modifier.fillMaxWidth(),
                isError = goalError != null,
                supportingText = { goalError?.let { Text(it) } },
                trailingIcon = {
                    FieldExplanation(explanation = stringResource(R.string.client_goal_desc))
                }
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = birthDate?.let { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it) } ?: "",
                    onValueChange = { },
                    label = { Text(stringResource(R.string.client_birthdate)) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    isError = birthDateError != null,
                    supportingText = { birthDateError?.let { Text(it) } },
                    trailingIcon = {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            FieldExplanation(explanation = stringResource(R.string.client_birthdate_desc))
                            Icon(Icons.Default.DateRange, contentDescription = "Select Birth Date")
                        }
                    }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(end = 48.dp)
                        .clickable { showBirthDatePicker = true }
                )
            }

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(stringResource(R.string.client_phone)) },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    FieldExplanation(explanation = stringResource(R.string.client_phone_desc))
                }
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = { Text(stringResource(R.string.client_email)) },
                modifier = Modifier.fillMaxWidth(),
                isError = emailError != null,
                supportingText = { emailError?.let { Text(it) } },
                trailingIcon = {
                    FieldExplanation(explanation = stringResource(R.string.client_email_desc))
                }
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = nextSession,
                    onValueChange = { },
                    label = { Text(nextSessionLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            FieldExplanation(explanation = stringResource(R.string.client_next_session_desc))
                            Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                        }
                    }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(end = 48.dp)
                        .clickable { showSessionDatePicker = true }
                )
            }

            // Subscription Plan Dropdown
            ExposedDropdownMenuBox(
                expanded = expandedPlans,
                onExpandedChange = { expandedPlans = !expandedPlans },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = subscriptionPlans.find { it.id == selectedPlanId }?.name ?: stringResource(R.string.select_subscription_plan),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.subscription_plans)) },
                    trailingIcon = {
                        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                            FieldExplanation(explanation = stringResource(R.string.client_plan_selection_desc))
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPlans)
                        }
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expandedPlans,
                    onDismissRequest = { expandedPlans = false }
                ) {
                    subscriptionPlans.forEach { plan ->
                        DropdownMenuItem(
                            text = { Text("${plan.name} (€${plan.price})") },
                            onClick = {
                                selectedPlanId = plan.id
                                expandedPlans = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    var isValid = true
                    if (name.isBlank()) {
                        nameError = nameRequiredError
                        isValid = false
                    }
                    if (goal.isBlank()) {
                        goalError = "Goal is required"
                        isValid = false
                    }
                    if (birthDate == null) {
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
                                birthDate = birthDate!!,
                                phone = phone,
                                email = email,
                                nextSession = nextSession,
                                subscriptionPlanId = selectedPlanId
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(saveClientText)
            }
        }
    }
}
