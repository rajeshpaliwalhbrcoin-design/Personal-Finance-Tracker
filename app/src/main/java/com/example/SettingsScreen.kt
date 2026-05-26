package com.example

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodels.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: FinanceViewModel, onBack: () -> Unit) {
    val currentLimit by viewModel.dailyLimit.collectAsStateWithLifecycle()
    val currentStartDay by viewModel.monthStartDay.collectAsStateWithLifecycle()

    var limitInput by remember { mutableStateOf(currentLimit.toString()) }
    var startDayInput by remember { mutableStateOf(currentStartDay.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = limitInput,
                onValueChange = { limitInput = it },
                label = { Text("Daily Limit") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = startDayInput,
                onValueChange = { startDayInput = it },
                label = { Text("Month Start Day (1-31)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val newLimit = limitInput.toDoubleOrNull() ?: currentLimit
                    val newStartDay = startDayInput.toIntOrNull()?.coerceIn(1, 31) ?: currentStartDay
                    viewModel.updateSettings(newLimit, newStartDay)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Settings")
            }
            
            Text("By default month starts at 1. Wait, if you set 26, it will calculate from 26 of current/previous month to next.", style = MaterialTheme.typography.bodySmall)
        }
    }
}
