package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.R
import com.example.personalgymapp.database.entity.ClientEntity
import com.example.personalgymapp.model.Payment
import com.example.personalgymapp.model.Subscription
import com.example.personalgymapp.model.TrainingSession
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailsScreen(
    client: ClientEntity?,
    subscriptions: List<Subscription>,
    trainingSessions: List<TrainingSession>,
    payments: List<Payment>,
    onAddPaymentClick: (Int) -> Unit,
    onAddSubscriptionClick: (Int) -> Unit,
    onEditClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(client?.name ?: stringResource(R.string.session_details)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (client != null) {
                        IconButton(onClick = { onEditClick(client.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
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
        if (client == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Text(text = "Client not found!", color = MaterialTheme.colorScheme.error)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                DetailItem(label = "Goal", value = client.goal)
                DetailItem(label = "Birth Date", value = sdf.format(client.birthDate))
                DetailItem(label = "Phone", value = client.phone.ifBlank { "Not provided" })
                DetailItem(label = "Email", value = client.email)
                
                val completedSessionsCount = trainingSessions.count { it.clientId == client.id && it.status == "Completed" }
                DetailItem(label = stringResource(R.string.sessions_completed).substringBefore(":"), value = completedSessionsCount.toString())

                DetailItem(label = stringResource(R.string.next_session).substringBefore(":"), value = client.nextSession)
                
                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.subscriptions),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    val clientSubscription = subscriptions.find { it.clientId == client.id }
                    if (clientSubscription == null) {
                        IconButton(onClick = { onAddSubscriptionClick(client.id) }) {
                            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_subscription))
                        }
                    }
                }

                val clientSubscription = subscriptions.find { it.clientId == client.id }
                if (clientSubscription != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = stringResource(R.string.client_plan, clientSubscription.planName), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Text(text = "${stringResource(R.string.status)}: ${clientSubscription.status}", color = if (clientSubscription.status == "Paid") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                            Text(text = "Owes: €${clientSubscription.balance}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Next Payment Due: ${clientSubscription.dueDate}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                } else {
                    Text(
                        text = "No active subscription found.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stringResource(R.string.payment_history)} (${payments.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = { onAddPaymentClick(client.id) }) {
                        Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_payment))
                    }
                }

                if (payments.isEmpty()) {
                    Text(text = stringResource(R.string.no_payments_yet), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.tertiary)
                } else {
                    payments.forEach { payment ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "€${payment.amount}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                                    Text(text = payment.date, style = MaterialTheme.typography.bodySmall)
                                }
                                if (payment.notes.isNotBlank()) {
                                    Text(text = payment.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
