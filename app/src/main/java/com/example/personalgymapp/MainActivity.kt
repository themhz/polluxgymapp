package com.example.personalgymapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.ViewModelProvider
import com.example.personalgymapp.database.AppDatabase
import com.example.personalgymapp.navigation.AppNavigation
import com.example.personalgymapp.repository.ClientRepository
import com.example.personalgymapp.ui.theme.PersonalGymAppTheme
import com.example.personalgymapp.viewmodel.ClientViewModel
import com.example.personalgymapp.viewmodel.ClientViewModelFactory
import com.example.personalgymapp.viewmodel.SettingsViewModel
import org.osmdroid.config.Configuration
import android.preference.PreferenceManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize OSMDroid
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        Configuration.getInstance().userAgentValue = packageName

        val database = AppDatabase.getDatabase(this)
        val repository = ClientRepository(
            database.clientDao(),
            database.subscriptionDao(),
            database.paymentDao(),
            database.exerciseDao(),
            database.workoutPlanDao(),
            database.trainingSessionDao(),
            database.sessionResultDao(),
            database.subscriptionPlanDao()
        )
        val viewModel = ViewModelProvider(this, ClientViewModelFactory(application, repository))[ClientViewModel::class.java]
        val settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        
        viewModel.seedDatabaseIfEmpty()

        enableEdgeToEdge()
        setContent {
            val settings by settingsViewModel.settings.collectAsState()
            
            PersonalGymAppTheme(
                primaryColor = Color(settings.primaryColor)
            ) {
                androidx.compose.material3.Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(clientViewModel = viewModel, settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}
