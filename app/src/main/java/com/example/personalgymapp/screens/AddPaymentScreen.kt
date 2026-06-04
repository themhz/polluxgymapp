package com.example.personalgymapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.personalgymapp.R
import com.example.personalgymapp.model.Payment
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPaymentScreen(
    clientId: Int,
    clientName: String,
    onSavePayment: (Payment) -> Unit,
    onBackClick: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var date by remember { 
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        mutableStateOf(sdf.format(Date())) 
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_payment_for, clientName)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.status_cancelled).substringBefore(" "))
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = amount,
                onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
                label = { Text(stringResource(R.string.amount_euro)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text(stringResource(R.string.date)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text(stringResource(R.string.notes_optional)) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val amountDouble = amount.toDoubleOrNull() ?: 0.0
                    if (amountDouble > 0) {
                        onSavePayment(
                            Payment(
                                clientId = clientId,
                                amount = amountDouble,
                                date = date,
                                notes = notes
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = amount.isNotBlank() && amount.toDoubleOrNull() != null
            ) {
                Text(stringResource(R.string.save_payment))
            }
        }
    }
}
