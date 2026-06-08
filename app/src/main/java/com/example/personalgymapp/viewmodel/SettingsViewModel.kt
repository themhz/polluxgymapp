package com.example.personalgymapp.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppSettings(
    val language: String = "en", // "en" or "el"
    val currency: String = "EUR", // "USD" or "EUR"
    val decimalSeparator: String = ".",
    val thousandSeparator: String = ",",
    val notificationsEnabled: Boolean = true,
    val isGoogleConnected: Boolean = false,
    val googleAccountName: String? = null
)

class SettingsViewModel : ViewModel() {
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

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

    fun connectGoogle(name: String) {
        _settings.value = _settings.value.copy(
            isGoogleConnected = true,
            googleAccountName = name
        )
    }

    fun disconnectGoogle() {
        _settings.value = _settings.value.copy(
            isGoogleConnected = false,
            googleAccountName = null
        )
    }
}
