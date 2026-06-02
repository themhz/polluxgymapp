package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarminSettingsScreen(onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val showPlaceholderMessage = {
        scope.launch {
            snackbarHostState.showSnackbar("Garmin integration will be added in a later phase.")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Garmin Settings") },
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Prepare Garmin integration for workouts and health data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

            // Section 1: Connection Status
            SettingsSection(title = "Connection Status") {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Status: Not connected",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Garmin connection is not available yet.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Section 2: Future Garmin Features
            SettingsSection(title = "Future Garmin Features") {
                val features = listOf(
                    "Import heart rate data",
                    "Import calories and activity data",
                    "Import sleep and recovery data",
                    "Import workout activity summaries",
                    "Link Garmin activity data to training sessions",
                    "Future: send workout plans to Garmin devices",
                    "Future: active workout companion on Garmin watch"
                )
                features.forEach { feature ->
                    FeatureRow(text = feature)
                }
            }

            // Section 3: Integration Type
            SettingsSection(title = "Integration Type") {
                IntegrationCard(
                    title = "Garmin Health API",
                    description = "Future cloud integration for health, wellness, and activity data after Garmin developer approval.",
                    icon = Icons.Default.Cloud
                )
                Spacer(modifier = Modifier.height(8.dp))
                IntegrationCard(
                    title = "Connect IQ Watch App",
                    description = "Future watch app integration for direct workout interaction on Garmin devices.",
                    icon = Icons.Default.Watch
                )
            }

            // Section 4: Developer Setup Checklist
            SettingsSection(title = "Developer Setup Checklist") {
                val checklist = listOf(
                    "Create Garmin Developer account",
                    "Apply for Garmin Connect Developer Program access",
                    "Decide required Garmin data types",
                    "Design backend for OAuth and token handling",
                    "Add privacy policy and user consent flow",
                    "Add real Garmin integration in a later phase"
                )
                checklist.forEach { item ->
                    ChecklistRow(text = item)
                }
            }

            // Section 5: Actions
            SettingsSection(title = "Actions") {
                Button(
                    onClick = { showPlaceholderMessage() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Connect Garmin")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { showPlaceholderMessage() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sync Garmin Data")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
fun FeatureRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun ChecklistRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun IntegrationCard(title: String, description: String, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
