package com.example.personalgymapp.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val sensorList = remember { sensorManager.getSensorList(Sensor.TYPE_ALL) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Device Sensors") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                )
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
                text = "Live Hardware Data",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(sensorList) { sensor ->
                    SensorValueCard(sensorManager, sensor)
                }
            }
        }
    }
}

fun getSensorDescription(type: Int): String {
    return when (type) {
        Sensor.TYPE_ACCELEROMETER -> "Measures acceleration force in m/s² on 3 axes. Used for motion detection (shaking, tilting)."
        Sensor.TYPE_AMBIENT_TEMPERATURE -> "Measures ambient room temperature in degrees Celsius (°C)."
        Sensor.TYPE_GRAVITY -> "Measures the force of gravity in m/s² applied to the device on 3 axes."
        Sensor.TYPE_GYROSCOPE -> "Measures the rate of rotation in rad/s around 3 axes. Used for rotation detection."
        Sensor.TYPE_LIGHT -> "Measures ambient light level (illumination) in lx. Used for auto-brightness."
        Sensor.TYPE_LINEAR_ACCELERATION -> "Measures acceleration force in m/s² on 3 axes, excluding gravity."
        Sensor.TYPE_MAGNETIC_FIELD -> "Measures ambient geomagnetic field in μT on 3 axes. Used for compass."
        Sensor.TYPE_PRESSURE -> "Measures ambient air pressure in hPa or mbar. Used for altitude changes."
        Sensor.TYPE_PROXIMITY -> "Measures proximity of an object in cm relative to the screen. Used for turning off screen during calls."
        Sensor.TYPE_RELATIVE_HUMIDITY -> "Measures relative ambient humidity in percent (%)."
        Sensor.TYPE_ROTATION_VECTOR -> "Measures orientation by providing the rotation vector elements."
        Sensor.TYPE_STEP_COUNTER -> "Measures the total number of steps taken since the last reboot."
        Sensor.TYPE_STEP_DETECTOR -> "Detects when the user takes a step."
        Sensor.TYPE_HEART_RATE -> "Measures heart rate in beats per minute (bpm)."
        Sensor.TYPE_HEART_BEAT -> "Detects every individual heart beat."
        else -> "Provides hardware-specific environmental or motion data."
    }
}

@Composable
fun SensorValueCard(sensorManager: SensorManager, sensor: Sensor) {
    var sensorValues by remember { mutableStateOf(floatArrayOf()) }

    // Use a DisposableEffect to register/unregister the listener
    DisposableEffect(sensor) {
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    sensorValues = it.values.copyOf()
                }
            }

            override fun onAccuracyChanged(s: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = sensor.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Type: ${sensor.stringType.substringAfterLast(".")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = getSensorDescription(sensor.type),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (sensorValues.isEmpty()) {
                Text(
                    text = "Waiting for data...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary
                )
            } else {
                sensorValues.forEachIndexed { index, value ->
                    Text(
                        text = "Value[$index]: ${"%.4f".format(value)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
