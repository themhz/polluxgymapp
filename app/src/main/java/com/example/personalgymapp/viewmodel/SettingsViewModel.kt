package com.example.personalgymapp.viewmodel

import android.app.Application
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.personalgymapp.util.GoogleDriveService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class AppSettings(
    val language: String = "en", // "en" or "el"
    val currency: String = "EUR", // "USD" or "EUR"
    val decimalSeparator: String = ".",
    val thousandSeparator: String = ",",
    val notificationsEnabled: Boolean = true,
    val isGoogleConnected: Boolean = false,
    val googleAccountName: String? = null,
    val primaryColor: Long = 0xFF00E5FF, // Default GymNeonCyan
    val isBackingUp: Boolean = false,
    val lastBackupDate: String? = null
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(application)
    private val driveService = GoogleDriveService(application)
    
    private val _settings = MutableStateFlow(AppSettings(
        primaryColor = sharedPrefs.getLong("theme_color", 0xFF00E5FF),
        isGoogleConnected = sharedPrefs.getBoolean("google_connected", false),
        googleAccountName = sharedPrefs.getString("google_account_name", null),
        lastBackupDate = sharedPrefs.getString("last_backup_date", null)
    ))
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private var currentAccount: GoogleSignInAccount? = null

    init {
        // Initialize language from current app locale
        val locales = AppCompatDelegate.getApplicationLocales()
        val currentLocale = if (!locales.isEmpty) {
            locales.get(0)?.language ?: "en"
        } else {
            "en"
        }
        _settings.value = _settings.value.copy(language = currentLocale)
    }

    fun updateLanguage(language: String) {
        if (_settings.value.language == language) return

        _settings.value = _settings.value.copy(language = language)
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(language)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    fun updateCurrency(currency: String) {
        _settings.value = _settings.value.copy(currency = currency)
    }

    fun updateDecimalSeparator(separator: String) {
        _settings.value = _settings.value.copy(decimalSeparator = separator)
    }

    fun updateThousandSeparator(separator: String) {
        _settings.value = _settings.value.copy(thousandSeparator = separator)
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        _settings.value = _settings.value.copy(notificationsEnabled = enabled)
    }

    fun updateThemeColor(color: Long) {
        _settings.value = _settings.value.copy(primaryColor = color)
        sharedPrefs.edit().putLong("theme_color", color).apply()
    }

    fun connectGoogle(account: GoogleSignInAccount) {
        currentAccount = account
        _settings.value = _settings.value.copy(
            isGoogleConnected = true,
            googleAccountName = account.email
        )
        sharedPrefs.edit()
            .putBoolean("google_connected", true)
            .putString("google_account_name", account.email)
            .apply()
    }

    fun disconnectGoogle() {
        currentAccount = null
        _settings.value = _settings.value.copy(
            isGoogleConnected = false,
            googleAccountName = null
        )
        sharedPrefs.edit()
            .putBoolean("google_connected", false)
            .remove("google_account_name")
            .apply()
    }

    fun backupToGoogleDrive(account: GoogleSignInAccount) {
        viewModelScope.launch {
            _settings.value = _settings.value.copy(isBackingUp = true)
            
            val dbPath = getApplication<Application>().getDatabasePath("personal_trainer_database").absolutePath
            val success = driveService.uploadDatabase(account, dbPath)
            
            if (success) {
                val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                _settings.value = _settings.value.copy(
                    isBackingUp = false,
                    lastBackupDate = dateStr
                )
                sharedPrefs.edit().putString("last_backup_date", dateStr).apply()
            } else {
                _settings.value = _settings.value.copy(isBackingUp = false)
            }
        }
    }
}
