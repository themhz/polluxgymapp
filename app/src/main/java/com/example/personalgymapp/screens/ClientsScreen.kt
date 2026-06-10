package com.example.personalgymapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.R
import com.example.personalgymapp.components.AddActionFab
import com.example.personalgymapp.database.entity.ClientEntity
import com.example.personalgymapp.database.entity.SubscriptionPlanEntity
import com.example.personalgymapp.model.Subscription

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientsScreen(
    clients: List<ClientEntity>,
    subscriptions: List<Subscription>,
    subscriptionPlans: List<SubscriptionPlanEntity>,
    onBackClick: () -> Unit,
    onClientClick: (Int) -> Unit,
    onAddClientClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedPlanFilter by remember { mutableStateOf("All Plans") }
    var expandedPlanFilter by remember { mutableStateOf(false) }

    val allPlanNames = listOf("All Plans") + subscriptionPlans.map { it.name }.distinct()

    val filteredClients = clients.filter { client ->
        val matchesSearch = client.name.contains(searchQuery, ignoreCase = true)
        val planName = subscriptionPlans.find { it.id == client.subscriptionPlanId }?.name ?: stringResource(R.string.no_plan)
        val matchesPlan = selectedPlanFilter == "All Plans" || planName == selectedPlanFilter
        matchesSearch && matchesPlan
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null
                        )
                        Text(stringResource(R.string.clients))
                    }
                },
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
        floatingActionButton = {
            AddActionFab(
                label = stringResource(R.string.add_client),
                onClick = onAddClientClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.manage_clients),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Search and Filter UI
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text(stringResource(R.string.search_by_name)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                val filterLabel = if (selectedPlanFilter == "All Plans") stringResource(R.string.all_plans) else selectedPlanFilter
                OutlinedTextField(
                    value = filterLabel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.filter_by_plan)) },
                    trailingIcon = {
                        IconButton(onClick = { expandedPlanFilter = !expandedPlanFilter }) {
                            Icon(
                                imageVector = if (expandedPlanFilter) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                DropdownMenu(
                    expanded = expandedPlanFilter,
                    onDismissRequest = { expandedPlanFilter = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    allPlanNames.forEach { plan ->
                        DropdownMenuItem(
                            text = { Text(if (plan == "All Plans") stringResource(R.string.all_plans) else plan) },
                            onClick = {
                                selectedPlanFilter = plan
                                expandedPlanFilter = false
                            }
                        )
                    }
                }
                // Transparent clickable layer to open dropdown on text click
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .padding(top = 8.dp)
                        .clickable { expandedPlanFilter = !expandedPlanFilter }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredClients.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (clients.isEmpty()) stringResource(R.string.no_clients_yet) else stringResource(R.string.no_matching_clients),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredClients) { client ->
                        val planName = subscriptionPlans.find { it.id == client.subscriptionPlanId }?.name ?: stringResource(R.string.no_plan)
                        ClientCard(
                            client = client,
                            planName = planName,
                            onClick = { onClientClick(client.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ClientCard(client: ClientEntity, planName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = client.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.client_plan, planName),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF008080)
                )
                Text(
                    text = client.goal,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.sessions_completed, client.sessionsCompleted),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    val nextSessionText = if (client.nextSession == "Not scheduled") {
                        stringResource(R.string.not_scheduled)
                    } else {
                        client.nextSession
                    }
                    Text(
                        text = stringResource(R.string.next_session, nextSessionText),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}
