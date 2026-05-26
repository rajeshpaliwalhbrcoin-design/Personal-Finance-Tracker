package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
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

@Composable
fun DashboardCard(viewModel: FinanceViewModel) {
    val limit by viewModel.dailyLimit.collectAsStateWithLifecycle()
    val todaySpend by viewModel.todaySpend.collectAsStateWithLifecycle()
    val averageSpend by viewModel.averageSpend.collectAsStateWithLifecycle()
    val debtStats by viewModel.debtStats.collectAsStateWithLifecycle()

    val remaining = limit - todaySpend

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
fun LendCard(viewModel: FinanceViewModel) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Lend Money", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Lend To - Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = { Text("Mobile Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Date (YYYY-MM-DD)") },
                placeholder = { Text("Leave empty for today") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    viewModel.addLending(name, amount, mobile, date)
                    name = ""
                    amount = ""
                    mobile = ""
                    date = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Add Lending")
            }
        }
    }
}

@Composable
fun ReceiveLendingCard(viewModel: FinanceViewModel) {
    var lendingId by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Mark Lending Received", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
            OutlinedTextField(
                value = lendingId,
                onValueChange = { lendingId = it },
                label = { Text("Lending ID") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount Received (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    viewModel.receiveLending(lendingId, amount)
                    lendingId = ""
                    amount = ""
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Mark Received")
            }
        }
    }
}
