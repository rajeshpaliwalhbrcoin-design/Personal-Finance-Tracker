package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodels.FinanceViewModel
import com.example.data.entities.Expense
import com.example.data.entities.Loan
import com.example.data.entities.Lending

@Composable
fun HomeScreen(viewModel: FinanceViewModel, paddingValues: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { DashboardCard(viewModel = viewModel) }
        item { ExpenseCard(viewModel = viewModel) }
        item { BorrowCard(viewModel = viewModel) }
        item { RepayCard(viewModel = viewModel) }
        item { LendCard(viewModel = viewModel) }
        item { ReceiveLendingCard(viewModel = viewModel) }
        item { TrackingLists(viewModel = viewModel) }
    }
}

// ... All the old components ...
// I will populate the components here.
