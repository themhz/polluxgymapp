package com.example.personalgymapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.R
import com.example.personalgymapp.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
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
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Google Authentication Section
            SettingsGroup(title = "Account & Sync", icon = Icons.Default.Cloud) {
                if (settings.isGoogleConnected) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Connected as", style = MaterialTheme.typography.labelSmall)
                            Text(text = settings.googleAccountName ?: "Google Account", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = { viewModel.disconnectGoogle() }) {
                            Text("Disconnect", color = MaterialTheme.colorScheme.error)
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Connect your Google account to sync data and enable future cloud features.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Button(
                            onClick = {
                                // Placeholder for actual Google Sign-In logic
                                // This requires a Web Client ID from Google Cloud Console
                                scope.launch {
                                    snackbarHostState.showSnackbar("Google Sign-In requires a Google Cloud Project ID. Set up keys in the next phase.")
                                    // Simulating successful connection for UI demonstration
                                    viewModel.connectGoogle("trainer.user@gmail.com")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Connect with Google")
                        }
                    }
                }
            }

            // Language Setting
            SettingsGroup(title = stringResource(R.string.language), icon = Icons.Default.Language) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.english), modifier = Modifier.weight(1f))
                    RadioButton(
                        selected = settings.language == "en",
                        onClick = { viewModel.updateLanguage("en") }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.greek), modifier = Modifier.weight(1f))
                    RadioButton(
                        selected = settings.language == "el",
                        onClick = { viewModel.updateLanguage("el") }
                    )
                }
            }

            // Currency Setting
            SettingsGroup(title = stringResource(R.string.currency), icon = Icons.Default.Payments) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.euro), modifier = Modifier.weight(1f))
                    RadioButton(
                        selected = settings.currency == "EUR",
                        onClick = { viewModel.updateCurrency("EUR") }
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.dollar), modifier = Modifier.weight(1f))
                    RadioButton(
                        selected = settings.currency == "USD",
                        onClick = { viewModel.updateCurrency("USD") }
                    )
                }
            }

            // Number Format Settings
            SettingsGroup(title = stringResource(R.string.number_format), icon = Icons.Default.Settings) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.decimal_separator), style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FilterChip(
                            selected = settings.decimalSeparator == ".",
                            onClick = { viewModel.updateDecimalSeparator(".") },
                            label = { Text(stringResource(R.string.dot)) }
                        )
                        FilterChip(
                            selected = settings.decimalSeparator == ",",
                            onClick = { viewModel.updateDecimalSeparator(",") },
                            label = { Text(stringResource(R.string.comma)) }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(stringResource(R.string.thousand_separator), style = MaterialTheme.typography.labelMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        FilterChip(
                            selected = settings.thousandSeparator == ",",
                            onClick = { viewModel.updateThousandSeparator(",") },
                            label = { Text(stringResource(R.string.comma)) }
                        )
                        FilterChip(
                            selected = settings.thousandSeparator == ".",
                            onClick = { viewModel.updateThousandSeparator(".") },
                            label = { Text(stringResource(R.string.dot)) }
                        )
                        FilterChip(
                            selected = settings.thousandSeparator == " ",
                            onClick = { viewModel.updateThousandSeparator(" ") },
                            label = { Text(stringResource(R.string.space)) }
                        )
                    }
                }
            }

            // Notifications Setting
            SettingsGroup(title = stringResource(R.string.notifications), icon = Icons.Default.Notifications) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.allow_notifications), modifier = Modifier.weight(1f))
                    Switch(
                        checked = settings.notificationsEnabled,
                        onCheckedChange = { viewModel.updateNotificationsEnabled(it) }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsGroup(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                content = content
            )
        }
    }
}
