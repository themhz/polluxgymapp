package com.example.personalgymapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.R
import com.example.personalgymapp.database.entity.ClientEntity
import com.example.personalgymapp.database.entity.SubscriptionPlanEntity
import com.example.personalgymapp.model.Subscription
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionScreen(
    clients: List<ClientEntity>,
    subscriptionPlans: List<SubscriptionPlanEntity>,
    initialClientId: Int = -1,
    selectedPlanIdFromNav: Int? = null,
    onNavigateToSelectPlan: () -> Unit,
    onNavigateToAddPlan: () -> Unit,
    onSaveSubscription: (Subscription) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedClient by remember { 
        mutableStateOf(clients.find { it.id == initialClientId }) 
    }
    var selectedPlan by remember { mutableStateOf<SubscriptionPlanEntity?>(null) }
    var planName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var totalPaid by remember { mutableStateOf("") }
    var dueDate by remember { 
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, 1)
        mutableStateOf(sdf.format(calendar.time)) 
    }

    var clientError by remember { mutableStateOf<String?>(null) }
    var planError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }

    var showNoPlansDialog by remember { mutableStateOf(subscriptionPlans.isEmpty()) }

    if (showNoPlansDialog) {
        AlertDialog(
            onDismissRequest = { /* Don't dismiss by clicking outside */ },
            icon = { Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Απαιτείται Πρόγραμμα Συνδρομής") },
            text = { 
                Text("Δεν έχετε δημιουργήσει ακόμα κάποιο template προγράμματος συνδρομής. " +
                     "Πρέπει πρώτα να φτιάξετε ένα πρόγραμμα (π.χ. 'Μηνιαίο') και μετά να το καταχωρήσετε στον πελάτη.") 
            },
            confirmButton = {
                Button(onClick = {
                    showNoPlansDialog = false
                    onNavigateToAddPlan()
                }) {
                    Text("Δημιουργία Προγράμματος")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showNoPlansDialog = false
                    onBackClick()
                }) {
                    Text("Πίσω")
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    // Effect to handle returning from plan selection screen
    LaunchedEffect(selectedPlanIdFromNav) {
        if (selectedPlanIdFromNav != null && selectedPlanIdFromNav != -1) {
            subscriptionPlans.find { it.id == selectedPlanIdFromNav }?.let { plan ->
                selectedPlan = plan
                planName = plan.name
                price = plan.price.toString()
                planError = null
                
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, plan.durationDays)
                dueDate = sdf.format(calendar.time)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_subscription)) },
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Section 1: Client Info
            SectionHeader(title = "Client Information", icon = Icons.Default.Person)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    var expandedClient by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expandedClient,
                        onExpandedChange = { expandedClient = !expandedClient }
                    ) {
                        OutlinedTextField(
                            value = selectedClient?.name ?: "Select Client",
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
                }
            }

            // Section 2: Subscription Plan
            SectionHeader(title = "Plan Details", icon = Icons.Default.Subscriptions)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = if (selectedPlan != null) "${selectedPlan!!.name} (€${selectedPlan!!.price})" else planName.ifBlank { "Select a template" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Subscription Plan Template") },
                            trailingIcon = { Icon(Icons.Default.Subscriptions, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                        
                        // Clickable overlay
                        Box(modifier = Modifier
                            .matchParentSize()
                            .clickable { onNavigateToSelectPlan() })
                    }

                    OutlinedTextField(
                        value = planName,
                        onValueChange = { planName = it; planError = null },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
                    )
                }
            }

            // Section 3: Payment & Dates
            SectionHeader(title = "Payment & Schedule", icon = Icons.Default.Payments)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it; priceError = null },
                            label = { Text("Price") },
                            modifier = Modifier.weight(1f),
                            prefix = { Text("€") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            isError = priceError != null,
                            supportingText = { priceError?.let { Text(it) } }
                        )
                        OutlinedTextField(
                            value = totalPaid,
                            onValueChange = { totalPaid = it },
                            label = { Text("Paid") },
                            modifier = Modifier.weight(1f),
                            prefix = { Text("€") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                    }

                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Renewal / Due Date") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        placeholder = { Text("YYYY-MM-DD") }
                    )
                }
            }

            // Summary Card
            if (selectedClient != null && planName.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Summary",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${selectedClient!!.name} will be assigned the \"$planName\" plan for €$price.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        val p = price.toDoubleOrNull() ?: 0.0
                        val tp = totalPaid.toDoubleOrNull() ?: 0.0
                        if (tp < p) {
                            Text(
                                text = "Remaining balance: €${String.format("%.2f", p - tp)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

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
                                id = 0,
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
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Confirm & Create Subscription", modifier = Modifier.padding(8.dp))
            }
            
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
