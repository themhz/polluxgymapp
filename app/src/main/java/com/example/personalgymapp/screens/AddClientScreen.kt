package com.example.personalgymapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
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
fun AddClientScreen(
    subscriptionPlans: List<SubscriptionPlanEntity>,
    onSaveClient: (ClientEntity) -> Unit,
    onBackClick: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var goal by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<Date?>(null) }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var selectedPlanId by remember { mutableStateOf<Int?>(null) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var goalError by remember { mutableStateOf<String?>(null) }
    var birthDateError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val birthDatePickerState = rememberDatePickerState()
    var showBirthDatePicker by remember { mutableStateOf(false) }

    var expandedPlans by remember { mutableStateOf(false) }

    val nameRequiredError = stringResource(R.string.error_name_required)
    val goalRequiredError = stringResource(R.string.error_goal_required)
    val birthDateRequiredError = stringResource(R.string.error_birthdate_required)
    val emailRequiredError = stringResource(R.string.error_email_required)

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
                    Text(stringResource(R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showBirthDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = birthDatePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_client)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.cancel))
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
                label = { Text(stringResource(R.string.client)) },
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
                            Icon(Icons.Default.DateRange, contentDescription = stringResource(R.string.client_birthdate))
                        }
                    }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(end = 48.dp) // Leave space for the icons
                        .clickable { showBirthDatePicker = true }
                )
            }

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(stringResource(R.string.phone_optional)) },
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
                        goalError = goalRequiredError
                        isValid = false
                    }
                    if (birthDate == null) {
                        birthDateError = birthDateRequiredError
                        isValid = false
                    }
                    if (email.isBlank()) {
                        emailError = emailRequiredError
                        isValid = false
                    }

                    if (isValid) {
                        val newClient = ClientEntity(
                            name = name,
                            goal = goal,
                            birthDate = birthDate!!,
                            phone = phone,
                            email = email,
                            sessionsCompleted = 0,
                            nextSession = "Not scheduled",
                            subscriptionPlanId = selectedPlanId
                        )
                        onSaveClient(newClient)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_client))
            }
        }
    }
}
