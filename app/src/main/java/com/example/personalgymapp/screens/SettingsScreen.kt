package com.example.personalgymapp.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.R
import com.example.personalgymapp.viewmodel.ClientViewModel
import com.example.personalgymapp.viewmodel.SettingsViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    clientViewModel: ClientViewModel,
    onBackClick: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showResetDialog by remember { mutableStateOf(false) }

    // Google Sign In Setup
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(DriveScopes.DRIVE_FILE))
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
            if (account != null) {
                viewModel.connectGoogle(account)
                scope.launch { snackbarHostState.showSnackbar("Connected to Google: ${account.email}") }
            }
        } catch (e: Exception) {
            scope.launch { snackbarHostState.showSnackbar("Sign-in failed: ${e.message}") }
        }
    }

    val themeColors = listOf(
        0xFF00E5FF, // Cyan
        0xFFFF5722, // Orange
        0xFF4CAF50, // Green
        0xFFE91E63, // Pink
        0xFFFFEB3B, // Yellow
        0xFF9C27B0, // Purple
        0xFF2196F3  // Blue
    )

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
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
                            IconButton(onClick = { 
                                googleSignInClient.signOut().addOnCompleteListener {
                                    viewModel.disconnectGoogle() 
                                }
                            }) {
                                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Disconnect", tint = MaterialTheme.colorScheme.error)
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        Column {
                            Text(text = "Google Drive Backup", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text(
                                text = if (settings.lastBackupDate != null) "Last backup: ${settings.lastBackupDate}" else "No backups yet",
                                style = MaterialTheme.typography.bodySmall
                            )
                            
                            Spacer(Modifier.height(8.dp))
                            
                            Button(
                                onClick = { 
                                    val account = GoogleSignIn.getLastSignedInAccount(context)
                                    if (account != null) {
                                        viewModel.backupToGoogleDrive(account)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !settings.isBackingUp,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                if (settings.isBackingUp) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Backing up...")
                                } else {
                                    Icon(Icons.Default.Backup, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Backup to Google Drive")
                                }
                            }
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.connect_google_desc),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Button(
                            onClick = { googleSignInLauncher.launch(googleSignInClient.signInIntent) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
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

            // Theme Color Setting
            SettingsGroup(title = stringResource(R.string.theme_color), icon = Icons.Default.Palette) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    themeColors.forEach { colorVal ->
                        val color = Color(colorVal)
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (settings.primaryColor == colorVal) 3.dp else 0.dp,
                                    color = if (settings.primaryColor == colorVal) Color.White else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { viewModel.updateThemeColor(colorVal) }
                        )
                    }
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
