package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.model.Subscription

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSubscriptionScreen(
    subscription: Subscription?,
    onSaveSubscription: (Subscription) -> Unit,
    onDeleteSubscription: (Subscription) -> Unit,
    onBackClick: () -> Unit
) {
    if (subscription == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Subscription not found")
        }
        return
    }

    var planName by remember { mutableStateOf(subscription.planName) }
    var price by remember { mutableStateOf(subscription.price.toString()) }
    var totalPaid by remember { mutableStateOf(subscription.totalPaid.toString()) }
    var dueDate by remember { mutableStateOf(subscription.dueDate) }

    var planError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Subscription") },
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
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Client: ${subscription.clientName}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            OutlinedTextField(
                value = planName,
                onValueChange = { planName = it; planError = null },
                label = { Text("Plan Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = planError != null,
                supportingText = { planError?.let { Text(it) } }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it; priceError = null },
                    label = { Text("Total Price (€)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = priceError != null,
                    supportingText = { priceError?.let { Text(it) } }
                )
                OutlinedTextField(
                    value = totalPaid,
                    onValueChange = { totalPaid = it },
                    label = { Text("Amount Paid (€)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }

            OutlinedTextField(
                value = dueDate,
                onValueChange = { dueDate = it },
                label = { Text("Due Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    var isValid = true
                    if (planName.isBlank()) {
                        planError = "Plan name is required"
                        isValid = false
                    }
                    val p = price.toDoubleOrNull() ?: 0.0
                    if (p <= 0) {
                        priceError = "Invalid price"
                        isValid = false
                    }

                    if (isValid) {
                        val tp = totalPaid.toDoubleOrNull() ?: 0.0
                        val status = when {
                            tp >= p -> "Paid"
                            else -> "Pending"
                        }
                        
                        onSaveSubscription(
                            subscription.copy(
                                planName = planName,
                                price = p,
                                totalPaid = tp,
                                dueDate = dueDate,
                                status = status
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Subscription")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Subscription") },
            text = { Text("Are you sure you want to delete this subscription for ${subscription.clientName}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteSubscription(subscription)
                        showDeleteDialog = false
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
}
