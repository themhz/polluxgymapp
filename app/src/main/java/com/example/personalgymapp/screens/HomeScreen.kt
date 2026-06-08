package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.R
import com.example.personalgymapp.components.AppLogo
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onClientsClick: () -> Unit,
    onWorkoutsClick: () -> Unit,
    onProgressClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onSensorsClick: () -> Unit,
    onGarminClick: () -> Unit,
    onSubscriptionsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                drawerContentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Power Cat Menu",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider()
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onSettingsClick() 
                    },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text("Garmin") },
                    selected = false,
                    onClick = { 
                        scope.launch { drawerState.close() }
                        onGarminClick() 
                    },
                    icon = { Icon(Icons.Default.Watch, contentDescription = null) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                // Add more items if needed
            }
        }
    ) {
        Scaffold(
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text(stringResource(R.string.app_name)) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Navigation Menu",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.settings),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary,
                        navigationIconContentColor = MaterialTheme.colorScheme.primary,
                        actionIconContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppLogo(size = 120.dp)
                
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.dashboard_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.dashboard_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
                
                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MenuCard(
                            title = stringResource(R.string.clients),
                            icon = Icons.Default.Person,
                            modifier = Modifier.weight(1f),
                            onClick = onClientsClick
                        )
                        MenuCard(
                            title = stringResource(R.string.subscription_plans),
                            icon = Icons.Default.Subscriptions,
                            modifier = Modifier.weight(1f),
                            onClick = onSubscriptionsClick
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MenuCard(
                            title = stringResource(R.string.workout_plans),
                            icon = Icons.AutoMirrored.Filled.Assignment,
                            modifier = Modifier.weight(1f),
                            onClick = onWorkoutsClick
                        )
                        MenuCard(
                            title = stringResource(R.string.training_sessions),
                            icon = Icons.Default.CalendarMonth,
                            modifier = Modifier.weight(1f),
                            onClick = onCalendarClick
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MenuCard(
                            title = stringResource(R.string.progress),
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            modifier = Modifier.weight(1f),
                            onClick = onProgressClick
                        )
                        MenuCard(
                            title = stringResource(R.string.sensors),
                            icon = Icons.Default.Sensors,
                            modifier = Modifier.weight(1f),
                            onClick = onSensorsClick
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MenuCard(
                            title = stringResource(R.string.garmin),
                            icon = Icons.Default.Watch,
                            modifier = Modifier.weight(1f),
                            onClick = onGarminClick
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun MenuCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(140.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    com.example.personalgymapp.ui.theme.PersonalGymAppTheme {
        HomeScreen(
            onClientsClick = {},
            onWorkoutsClick = {},
            onProgressClick = {},
            onCalendarClick = {},
            onSensorsClick = {},
            onGarminClick = {},
            onSubscriptionsClick = {},
            onSettingsClick = {}
        )
    }
}
