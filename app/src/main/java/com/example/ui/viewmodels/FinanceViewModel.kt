package com.example.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FinanceDatabase
import com.example.data.FinanceRepository
import com.example.data.dao.DailyExpense
import com.example.data.dao.DebtStats
import com.example.data.entities.Expense
import com.example.data.entities.Loan
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository

    init {
        val db = FinanceDatabase.getDatabase(application)
        repository = FinanceRepository(db)
    }

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    private val todayString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private val currentMonthString: String
        get() = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

    val todaySpend: StateFlow<Double> = repository.getTodaySpend(todayString)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthlySpend: StateFlow<Double> = repository.getMonthSpend(currentMonthString)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val dailyExpenses: StateFlow<List<DailyExpense>> = repository.getDailyExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeLoans: StateFlow<List<Loan>> = repository.getActiveLoans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todaysExpensesList: StateFlow<List<Expense>> = repository.getExpensesForDate(todayString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debtStats: StateFlow<DebtStats> = repository.getDebtStats()
        .map { it ?: DebtStats(0.0, 0) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DebtStats(0.0, 0))

    val averageSpend: StateFlow<Double> = monthlySpend
        .map { it / 30.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addExpense(amount: String, note: String) {
        val amt = amount.toDoubleOrNull()
        if (amt == null) {
            showMessage("Please enter a valid amount")
            return
        }
        val finalNote = if (note.isBlank()) "General" else note
        viewModelScope.launch {
            try {
                repository.insertExpense(Expense(amount = amt, note = finalNote, date = todayString))
                showMessage("Expense added")
            } catch (e: Exception) {
                showMessage("Error adding expense")
            }
        }
    }

    fun addLoan(name: String, amount: String) {
        if (name.isBlank()) {
            showMessage("Enter lender name")
            return
        }
        val amt = amount.toDoubleOrNull()
        if (amt == null) {
            showMessage("Please enter a valid amount")
            return
        }
        viewModelScope.launch {
            try {
                repository.insertLoan(Loan(name = name, amount = amt))
                showMessage("Debt logged")
            } catch (e: Exception) {
                showMessage("Error adding loan")
            }
        }
    }

    fun payLoan(loanId: String, amount: String) {
        val lId = loanId.toIntOrNull()
        val amt = amount.toDoubleOrNull()
        if (lId == null || amt == null) {
            showMessage("Enter Loan ID and Amount")
            return
        }
        viewModelScope.launch {
            try {
                val success = repository.recordRepayment(lId, amt)
                if (success) {
                    showMessage("Repayment recorded")
                } else {
                    showMessage("Loan ID not found")
                }
            } catch (e: Exception) {
                showMessage("Error recording repayment")
            }
        }
    }

    private fun showMessage(msg: String) {
        viewModelScope.launch {
            _snackbarMessage.emit(msg)
        }
    }
}
