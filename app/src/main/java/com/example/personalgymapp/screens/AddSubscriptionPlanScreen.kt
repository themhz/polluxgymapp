package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.R
import com.example.personalgymapp.components.FieldExplanation
import com.example.personalgymapp.database.entity.SubscriptionPlanEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionPlanScreen(
    onSavePlan: (SubscriptionPlanEntity) -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var durationType by remember { mutableStateOf("Monthly") } // "Monthly" or "Days"
    var durationValue by remember { mutableStateOf("1") }
    var description by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var durationError by remember { mutableStateOf<String?>(null) }

    val nameRequiredError = stringResource(R.string.error_name_required)
    val invalidPriceError = stringResource(R.string.error_invalid_price)
    val invalidDurationError = stringResource(R.string.error_invalid_duration)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_subscription_plan)) },
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
            // Explanatory Message
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.add_plan_info),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                label = { Text(stringResource(R.string.plan_name)) },
                placeholder = { Text(stringResource(R.string.plan_name_hint)) },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it) } },
                trailingIcon = {
                    FieldExplanation(explanation = stringResource(R.string.plan_name_desc))
                }
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it; priceError = null },
                label = { Text("${stringResource(R.string.price)} (€)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = priceError != null,
                supportingText = { priceError?.let { Text(it) } },
                trailingIcon = {
                    FieldExplanation(explanation = stringResource(R.string.plan_price_desc))
                }
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.plan_duration),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(8.dp))
                    FieldExplanation(explanation = stringResource(R.string.plan_duration_type_desc))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = durationType == "Monthly",
                        onClick = { 
                            durationType = "Monthly"
                            if (durationValue.isEmpty()) durationValue = "1"
                        },
                        label = { Text(stringResource(R.string.monthly)) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = durationType == "Days",
                        onClick = { 
                            durationType = "Days"
                            if (durationValue.isEmpty()) durationValue = "30"
                        },
                        label = { Text(stringResource(R.string.days)) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            OutlinedTextField(
                value = durationValue,
                onValueChange = { durationValue = it; durationError = null },
                label = { Text(if (durationType == "Monthly") stringResource(R.string.number_of_months) else stringResource(R.string.number_of_days)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = durationError != null,
                supportingText = { durationError?.let { Text(it) } },
                trailingIcon = {
                    FieldExplanation(explanation = stringResource(R.string.plan_duration_value_desc))
                }
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description_optional)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                trailingIcon = {
                    FieldExplanation(explanation = stringResource(R.string.plan_description_desc))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    var isValid = true
                    if (name.isBlank()) {
                        nameError = nameRequiredError
                        isValid = false
                    }
                    val priceValue = price.toDoubleOrNull()
                    if (priceValue == null) {
                        priceError = invalidPriceError
                        isValid = false
                    }
                    val durationInt = durationValue.toIntOrNull()
                    if (durationInt == null || durationInt <= 0) {
                        durationError = invalidDurationError
                        isValid = false
                    }

                    if (isValid) {
                        val finalDays = if (durationType == "Monthly") durationInt!! * 30 else durationInt!!
                        onSavePlan(
                            SubscriptionPlanEntity(
                                name = name,
                                price = priceValue!!,
                                durationDays = finalDays,
                                description = description
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_plan))
            }
        }
    }
}
