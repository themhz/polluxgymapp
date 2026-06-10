package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    onDeletePaymentClick: (Payment) -> Unit,
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
                DetailItem(label = stringResource(R.string.client_goal), value = client.goal)
                DetailItem(label = stringResource(R.string.client_birthdate), value = sdf.format(client.birthDate))
                DetailItem(label = stringResource(R.string.client_phone), value = client.phone.ifBlank { stringResource(R.string.not_scheduled) })
                DetailItem(label = stringResource(R.string.client_email), value = client.email)
                
                val completedSessionsCount = trainingSessions.count { it.clientId == client.id && it.status == "Completed" }
                DetailItem(label = stringResource(R.string.sessions_completed).substringBefore(":"), value = completedSessionsCount.toString())

                val sessionLabel = stringResource(R.string.next_session_label)
                val nextSessionValue = if (client.nextSession == "Not scheduled") stringResource(R.string.not_scheduled) else client.nextSession
                Column {
                    DetailItem(label = sessionLabel, value = nextSessionValue)
                    Text(
                        text = stringResource(R.string.client_next_session_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
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
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF008080),
                            contentColor = Color.White
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = stringResource(R.string.client_plan, clientSubscription.planName),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            val statusText = when(clientSubscription.status) {
                                "Paid" -> stringResource(R.string.paid)
                                "Pending" -> stringResource(R.string.pending)
                                "Overdue" -> stringResource(R.string.overdue)
                                else -> clientSubscription.status
                            }
                            Text(
                                text = "${stringResource(R.string.status)}: $statusText",
                                color = if (clientSubscription.status == "Paid") Color(0xFFE0F2F1) else Color(0xFFFFEBEE)
                            )
                            Text(
                                text = stringResource(R.string.owes, "€${clientSubscription.balance}"),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(R.string.next_payment_due, clientSubscription.dueDate),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    Text(
                        text = stringResource(R.string.no_active_subscription),
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (payment.notes.isNotBlank()) {
                                        Text(
                                            text = payment.notes,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.secondary,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    }
                                    IconButton(onClick = { onDeletePaymentClick(payment) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Payment",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
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
