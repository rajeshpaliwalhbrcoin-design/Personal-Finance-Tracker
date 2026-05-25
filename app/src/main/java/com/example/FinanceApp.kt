package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.dao.DailyExpense
import com.example.data.entities.Expense
import com.example.data.entities.Loan
import com.example.ui.viewmodels.FinanceViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceApp(viewModel: FinanceViewModel) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance Tracker", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
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
            item { TrackingLists(viewModel = viewModel) }
        }
    }
}

@Composable
fun DashboardCard(viewModel: FinanceViewModel) {
    val limit = 230.0
    val todaySpend by viewModel.todaySpend.collectAsStateWithLifecycle()
    val averageSpend by viewModel.averageSpend.collectAsStateWithLifecycle()
    val monthlySpend by viewModel.monthlySpend.collectAsStateWithLifecycle()
    val debtStats by viewModel.debtStats.collectAsStateWithLifecycle()

    val remaining = limit - todaySpend
    val onTrack = remaining >= 0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFDBEAFE)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE3F2FD)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Cash Flow Protection", style = MaterialTheme.typography.titleMedium, color = Color(0xFF1E3A8A), fontWeight = FontWeight.Bold)
            Text(
                "Limit: ₹$limit / day\nSpent Today: ₹$todaySpend\nRemaining: ₹$remaining\nAvg: ₹${"%.2f".format(averageSpend)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF2563EB),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { if (limit > 0) (todaySpend / limit).toFloat().coerceIn(0f, 1f) else 0f },
                color = Color(0xFF2563EB),
                trackColor = Color(0xFFBFDBFE),
                modifier = Modifier.fillMaxWidth().height(8.dp),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFFFEE2E2)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Active Debt", style = MaterialTheme.typography.titleSmall, color = Color(0xFF7F1D1D), fontWeight = FontWeight.Bold)
                    Text(
                        "${debtStats.activeLoansCount} Active Loans • Total: ₹${debtStats.totalDebt}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB91C1C)
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseCard(viewModel: FinanceViewModel) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Log Expense", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount spent (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = "Currency") },
                modifier = Modifier.fillMaxWidth().testTag("exp_amt")
            )
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (food, rent, etc.)") },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = "Note") },
                modifier = Modifier.fillMaxWidth().testTag("exp_note")
            )
            Button(
                onClick = {
                    viewModel.addExpense(amount, note)
                    amount = ""
                    note = ""
                },
                modifier = Modifier.align(Alignment.CenterHorizontally).testTag("add_expense_btn")
            ) {
                Text("Add Expense")
            }
        }
    }
}

@Composable
fun BorrowCard(viewModel: FinanceViewModel) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Log Borrowed Money", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Lender Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Person") },
                modifier = Modifier.fillMaxWidth().testTag("loan_name")
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount Borrowed (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = "Currency") },
                modifier = Modifier.fillMaxWidth().testTag("loan_amt")
            )
            Button(
                onClick = {
                    viewModel.addLoan(name, amount)
                    name = ""
                    amount = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.align(Alignment.CenterHorizontally).testTag("add_debt_btn")
            ) {
                Text("Add Debt")
            }
        }
    }
}

@Composable
fun RepayCard(viewModel: FinanceViewModel) {
    var loanId by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Record Repayment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
            OutlinedTextField(
                value = loanId,
                onValueChange = { loanId = it },
                label = { Text("Loan ID") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = "ID") },
                modifier = Modifier.fillMaxWidth().testTag("pay_id")
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount Repaid (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = "Amount") },
                modifier = Modifier.fillMaxWidth().testTag("pay_amt")
            )
            Button(
                onClick = {
                    viewModel.payLoan(loanId, amount)
                    loanId = ""
                    amount = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                modifier = Modifier.align(Alignment.CenterHorizontally).testTag("mark_repayment_btn")
            ) {
                Text("Mark Repayment")
            }
        }
    }
}

@Composable
fun TrackingLists(viewModel: FinanceViewModel) {
    val activeLoans by viewModel.activeLoans.collectAsStateWithLifecycle()
    val todaysExpenses by viewModel.todaysExpensesList.collectAsStateWithLifecycle()
    val dailyExpenses by viewModel.dailyExpenses.collectAsStateWithLifecycle()

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Active Debts
        Text("Active Debts", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(0.dp)) {
                if (activeLoans.isEmpty()) {
                    ListItem(headlineContent = { Text("No active debts!") }, supportingContent = { Text("You are clear.") })
                } else {
                    activeLoans.forEach { loan ->
                        val left = loan.amount - loan.paid
                        ListItem(
                            headlineContent = { Text("ID: ${loan.id} | ${loan.name}") },
                            supportingContent = { Text("Borrowed: ₹${loan.amount} | Paid: ₹${loan.paid}\nLeft: ₹$left") }
                        )
                        Divider()
                    }
                }
            }
        }

        // Today's Expenses
        Text("Today's Expenses", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(0.dp)) {
                if (todaysExpenses.isEmpty()) {
                    ListItem(headlineContent = { Text("No expenses today.") }, supportingContent = { Text("Keep it up!") })
                } else {
                    todaysExpenses.forEach { exp ->
                        ListItem(
                            headlineContent = { Text("₹${exp.amount}") },
                            supportingContent = { Text(exp.note) }
                        )
                        Divider()
                    }
                }
            }
        }

        // Day Wise Expenses
        Text("Day Wise Expenses", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(0.dp)) {
                if (dailyExpenses.isEmpty()) {
                    ListItem(headlineContent = { Text("No expense history found.") })
                } else {
                    dailyExpenses.forEach { dex ->
                        ListItem(
                            headlineContent = { Text("Date - ${dex.date}") },
                            supportingContent = { Text("Total Expend - ₹${dex.totalAmount}\nDaily Limit - ₹230") }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}
