package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.database.entity.ClientEntity
import com.example.personalgymapp.model.Subscription
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionScreen(
    clients: List<ClientEntity>,
    onSaveSubscription: (Subscription) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedClient by remember { mutableStateOf<ClientEntity?>(null) }
    var planName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var totalPaid by remember { mutableStateOf("") }
    var dueDate by remember { 
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        mutableStateOf(sdf.format(Date())) 
    }

    var clientError by remember { mutableStateOf<String?>(null) }
    var planError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Subscription") },
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
                .padding(innerPadding)
                .fillMaxSize()
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
                    value = selectedClient?.name ?: "Select Client",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Client") },
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

            OutlinedTextField(
                value = planName,
                onValueChange = { planName = it; planError = null },
                label = { Text("Plan Name (e.g. 12 Session Pack)") },
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
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("e.g. 2024-06-15") }
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    var isValid = true
                    if (selectedClient == null) {
                        clientError = "Client is required"
                        isValid = false
                    }
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
                            Subscription(
                                id = 0, // ID will be assigned in AppNavigation
                                clientId = selectedClient!!.id,
                                clientName = selectedClient!!.name,
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
                Text("Create Subscription")
            }
        }
    }
}
