package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.database.entity.ClientEntity
import com.example.personalgymapp.model.Subscription
import com.example.personalgymapp.model.TrainingSession
import com.example.personalgymapp.model.WorkoutPlan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDetailsScreen(
    client: ClientEntity?,
    subscriptions: List<Subscription>,
    workoutPlans: List<WorkoutPlan>,
    trainingSessions: List<TrainingSession>,
    onEditClick: (Int) -> Unit,
    onWorkoutPlanClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(client?.name ?: "Client Details") },
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
                DetailItem(label = "Goal", value = client.goal)
                DetailItem(label = "Birth Date", value = client.birthDate)
                DetailItem(label = "Phone", value = client.phone.ifBlank { "Not provided" })
                DetailItem(label = "Email", value = client.email)
                
                val completedSessionsCount = trainingSessions.count { it.clientId == client.id && it.status == "Completed" }
                DetailItem(label = "Workout Sessions Completed", value = completedSessionsCount.toString())

                DetailItem(label = "Next Session", value = client.nextSession)
                
                HorizontalDivider()

                Text(
                    text = "Subscription Status",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                val clientSubscription = subscriptions.find { it.clientId == client.id }
                if (clientSubscription != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Plan: ${clientSubscription.planName}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            Text(text = "Status: ${clientSubscription.status}", color = if (clientSubscription.status == "Paid") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                            Text(text = "Owes: €${clientSubscription.balance}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Next Payment Due: ${clientSubscription.dueDate}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                } else {
                    Text(text = "No active subscription found.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.tertiary)
                }

                HorizontalDivider()
                
                Text(
                    text = "Client Workout Plans",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                val clientPlans = workoutPlans.filter { it.clientId == client.id }
                
                if (clientPlans.isEmpty()) {
                    Text(
                        text = "No workout plans created for this client.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                } else {
                    clientPlans.forEach { plan ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onWorkoutPlanClick(plan.id) },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = plan.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${plan.exercises.size} Exercises",
                                    style = MaterialTheme.typography.bodySmall
                                )
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
