package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.database.entity.SubscriptionPlanEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubscriptionPlanScreen(
    plan: SubscriptionPlanEntity?,
    onSavePlan: (SubscriptionPlanEntity) -> Unit,
    onDeletePlan: (SubscriptionPlanEntity) -> Unit,
    onBackClick: () -> Unit
) {
    if (plan == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Plan not found")
        }
        return
    }

    var name by remember { mutableStateOf(plan.name) }
    var price by remember { mutableStateOf(plan.price.toString()) }
    var durationType by remember { 
        mutableStateOf(if (plan.durationDays % 30 == 0) "Monthly" else "Days") 
    }
    var durationValue by remember { 
        mutableStateOf(if (plan.durationDays % 30 == 0) (plan.durationDays / 30).toString() else plan.durationDays.toString()) 
    }
    var description by remember { mutableStateOf(plan.description) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var durationError by remember { mutableStateOf<String?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Plan") },
            text = { Text("Are you sure you want to delete this subscription plan?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeletePlan(plan)
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Subscription Plan") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
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
                        text = "Τροποποιήστε τα στοιχεία του πακέτου συνδρομής. Οι αλλαγές θα επηρεάσουν μόνο τις μελλοντικές αναθέσεις αυτού του πλάνου.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                label = { Text("Plan Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it) } }
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it; priceError = null },
                label = { Text("Price (€)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = priceError != null,
                supportingText = { priceError?.let { Text(it) } }
            )

            Text(
                text = "Plan Duration",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = durationType == "Monthly",
                    onClick = { durationType = "Monthly" },
                    label = { Text("Monthly") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = durationType == "Days",
                    onClick = { durationType = "Days" },
                    label = { Text("Days") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = durationValue,
                onValueChange = { durationValue = it; durationError = null },
                label = { Text(if (durationType == "Monthly") "Number of Months" else "Number of Days") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = durationError != null,
                supportingText = { durationError?.let { Text(it) } }
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    var isValid = true
                    if (name.isBlank()) {
                        nameError = "Name is required"
                        isValid = false
                    }
                    val priceValue = price.toDoubleOrNull()
                    if (priceValue == null) {
                        priceError = "Invalid price"
                        isValid = false
                    }
                    val durationInt = durationValue.toIntOrNull()
                    if (durationInt == null || durationInt <= 0) {
                        durationError = "Invalid duration"
                        isValid = false
                    }

                    if (isValid) {
                        val finalDays = if (durationType == "Monthly") durationInt!! * 30 else durationInt!!
                        onSavePlan(
                            plan.copy(
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
                Text("Save Changes")
            }
        }
    }
}
