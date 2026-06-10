package com.example.personalgymapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.R
import com.example.personalgymapp.viewmodel.ClientViewModel
import com.example.personalgymapp.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    clientViewModel: ClientViewModel,
    onBackClick: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showResetDialog by remember { mutableStateOf(false) }

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
            SettingsGroup(title = stringResource(R.string.account_sync), icon = Icons.Default.Cloud) {
                if (settings.isGoogleConnected) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(R.string.connected_as), style = MaterialTheme.typography.labelSmall)
                            Text(text = settings.googleAccountName ?: stringResource(R.string.google_account), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                        TextButton(onClick = { viewModel.disconnectGoogle() }) {
                            Text(stringResource(R.string.disconnect), color = MaterialTheme.colorScheme.error)
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val setupMsg = stringResource(R.string.google_signin_setup_msg)
                        Text(
                            text = stringResource(R.string.connect_google_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Button(
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(setupMsg)
                                    viewModel.connectGoogle("trainer.user@gmail.com")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(stringResource(R.string.connect_with_google))
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

            // Danger Zone - Reset Database
            SettingsGroup(title = stringResource(R.string.danger_zone), icon = Icons.Default.Warning) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = stringResource(R.string.reset_db_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = { showResetDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.reset_database))
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(stringResource(R.string.reset_db_confirm_title), color = MaterialTheme.colorScheme.error) },
            text = { Text(stringResource(R.string.reset_db_confirm_msg)) },
            confirmButton = {
                Button(
                    onClick = {
                        clientViewModel.resetDatabase()
                        showResetDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Database Reset Successfully")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
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
