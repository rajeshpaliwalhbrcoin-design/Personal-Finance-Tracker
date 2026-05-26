package com.example

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodels.FinanceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: FinanceViewModel, onBack: () -> Unit) {
    val dailyExpenses by viewModel.dailyExpenses.collectAsStateWithLifecycle()
    val debtStats by viewModel.debtStats.collectAsStateWithLifecycle()
    val lendingStats by viewModel.lendingStats.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard") },
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
            Text("Expense Chart (Last 7 Days)", style = MaterialTheme.typography.titleMedium)
            
            // Simple Bar Chart for daily expenses
            if (dailyExpenses.isNotEmpty()) {
                val last7Days = dailyExpenses.take(7).reversed()
                val maxAmount = last7Days.maxOfOrNull { it.totalAmount }?.takeIf { it > 0 } ?: 1.0
                
                Canvas(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)) {
                    val barWidth = size.width / (last7Days.size * 2)
                    var xOffset = barWidth / 2
                    
                    for (day in last7Days) {
                        val barHeight = (day.totalAmount / maxAmount).toFloat() * size.height
                        drawRect(
                            color = Color(0xFF2563EB),
                            topLeft = Offset(xOffset, size.height - barHeight),
                            size = Size(barWidth, barHeight)
                        )
                        xOffset += barWidth * 2
                    }
                }
            } else {
                Text("No data to show chart")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Debts Overview", style = MaterialTheme.typography.titleMedium)
            Text("Total Owed: ₹${debtStats.totalDebt}")
            
            Spacer(modifier = Modifier.height(16.dp))

            Text("Lendings Overview", style = MaterialTheme.typography.titleMedium)
            Text("Total Lent out: ₹${lendingStats.totalLentStr}")
        }
    }
}
