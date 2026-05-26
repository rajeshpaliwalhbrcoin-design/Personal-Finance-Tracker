package com.example

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodels.FinanceViewModel
import com.example.data.entities.Expense
import com.example.data.entities.Loan
import com.example.data.entities.Lending

@Composable
fun TrackingLists(viewModel: FinanceViewModel) {
    val activeLoans by viewModel.activeLoans.collectAsStateWithLifecycle()
    val todaysExpenses by viewModel.todaysExpensesList.collectAsStateWithLifecycle()
    val dailyExpenses by viewModel.dailyExpenses.collectAsStateWithLifecycle()
    val activeLendings by viewModel.activeLendings.collectAsStateWithLifecycle()

    var editingExpense by remember { mutableStateOf<Expense?>(null) }
    var editingLoan by remember { mutableStateOf<Loan?>(null) }
    var editingLending by remember { mutableStateOf<Lending?>(null) }

    if (editingExpense != null) {
        EditExpenseDialog(
            expense = editingExpense!!,
            onDismiss = { editingExpense = null },
            onSave = { updated ->
                viewModel.updateExpense(updated)
                editingExpense = null
            }
        )
    }

    if (editingLoan != null) {
        EditLoanDialog(
            loan = editingLoan!!,
            onDismiss = { editingLoan = null },
            onSave = { updated ->
                viewModel.updateLoan(updated)
                editingLoan = null
            }
        )
    }

    if (editingLending != null) {
        EditLendingDialog(
            lending = editingLending!!,
            onDismiss = { editingLending = null },
            onSave = { updated ->
                viewModel.updateLending(updated)
                editingLending = null
            }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        CollapsibleSection(title = "Today's Expenses") {
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
                                supportingContent = { Text(exp.note) },
                                trailingContent = {
                                    Row {
                                        IconButton(onClick = { editingExpense = exp }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                                        }
                                        IconButton(onClick = { viewModel.deleteExpense(exp) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                        }
                                    }
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        CollapsibleSection(title = "Active Debts") {
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
                                supportingContent = { Text("Borrowed: ₹${loan.amount} | Paid: ₹${loan.paid}\nLeft: ₹$left") },
                                trailingContent = {
                                    Row {
                                        IconButton(onClick = { editingLoan = loan }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                                        }
                                        IconButton(onClick = { viewModel.deleteLoan(loan) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                        }
                                    }
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        CollapsibleSection(title = "Active Lendings") {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(0.dp)) {
                    if (activeLendings.isEmpty()) {
                        ListItem(headlineContent = { Text("No active lendings!") })
                    } else {
                        activeLendings.forEach { lend ->
                            val left = lend.amount - lend.received
                            ListItem(
                                headlineContent = { Text("ID: ${lend.id} | ${lend.name}") },
                                supportingContent = { Text("Lent: ₹${lend.amount} | Received: ₹${lend.received}\nLeft: ₹$left | Ph: ${lend.mobileNumber}\nDate: ${lend.date}") },
                                trailingContent = {
                                    Row {
                                        IconButton(onClick = { editingLending = lend }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                                        }
                                        IconButton(onClick = { viewModel.deleteLending(lend) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                        }
                                    }
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        CollapsibleSection(title = "Day Wise Expenses") {
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
                        val limit by viewModel.dailyLimit.collectAsStateWithLifecycle()
                        dailyExpenses.forEach { dex ->
                            ListItem(
                                headlineContent = { Text("Date - ${dex.date}") },
                                supportingContent = { Text("Total Expend - ₹${dex.totalAmount}\nDaily Limit - ₹$limit") }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CollapsibleSection(title: String, content: @Composable () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (expanded) {
            content()
        }
    }
}

@Composable
fun EditExpenseDialog(expense: Expense, onDismiss: () -> Unit, onSave: (Expense) -> Unit) {
    var amount by remember { mutableStateOf(expense.amount.toString()) }
    var note by remember { mutableStateOf(expense.note) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Expense") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { 
                amount.toDoubleOrNull()?.let { 
                    onSave(expense.copy(amount = it, note = note))
                } 
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditLoanDialog(loan: Loan, onDismiss: () -> Unit, onSave: (Loan) -> Unit) {
    var amount by remember { mutableStateOf(loan.amount.toString()) }
    var name by remember { mutableStateOf(loan.name) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Loan") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Lender Name") }
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount Borrowed (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { 
                amount.toDoubleOrNull()?.let { 
                    onSave(loan.copy(amount = it, name = name))
                } 
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditLendingDialog(lending: Lending, onDismiss: () -> Unit, onSave: (Lending) -> Unit) {
    var amount by remember { mutableStateOf(lending.amount.toString()) }
    var name by remember { mutableStateOf(lending.name) }
    var mobile by remember { mutableStateOf(lending.mobileNumber) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Lending") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Lend To - Name") }
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = mobile,
                    onValueChange = { mobile = it },
                    label = { Text("Mobile Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { 
                amount.toDoubleOrNull()?.let { 
                    onSave(lending.copy(amount = it, name = name, mobileNumber = mobile))
                } 
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
