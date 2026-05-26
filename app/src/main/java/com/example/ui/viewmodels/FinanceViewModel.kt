package com.example.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FinanceDatabase
import com.example.data.FinanceRepository
import com.example.data.SettingsManager
import com.example.data.dao.DailyExpense
import com.example.data.dao.DebtStats
import com.example.data.dao.LendingStats
import com.example.data.entities.Expense
import com.example.data.entities.Lending
import com.example.data.entities.Loan
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: FinanceRepository
    val settingsManager = SettingsManager(application)

    private val _dailyLimit = MutableStateFlow(settingsManager.getDailyLimit())
    val dailyLimit: StateFlow<Double> = _dailyLimit.asStateFlow()

    private val _monthStartDay = MutableStateFlow(settingsManager.getMonthStartDay())
    val monthStartDay: StateFlow<Int> = _monthStartDay.asStateFlow()

    init {
        val db = FinanceDatabase.getDatabase(application)
        repository = FinanceRepository(db)
    }

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    private val todayString: String
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    val todaySpend: StateFlow<Double> = repository.getTodaySpend(todayString)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val expenseDateRange = _monthStartDay.map { startDay ->
        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        
        val startCal = Calendar.getInstance()
        if (currentDay < startDay) {
            startCal.add(Calendar.MONTH, -1)
        }
        startCal.set(Calendar.DAY_OF_MONTH, startDay)
        
        val endCal = startCal.clone() as Calendar
        endCal.add(Calendar.MONTH, 1)
        endCal.add(Calendar.DAY_OF_MONTH, -1)

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        Pair(format.format(startCal.time), format.format(endCal.time))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Pair(todayString, todayString))

    val monthlySpend: StateFlow<Double> = expenseDateRange.flatMapLatest { (start, end) ->
        repository.getSpendBetweenDates(start, end).map { it ?: 0.0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val dailyExpenses: StateFlow<List<DailyExpense>> = repository.getDailyExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExpenses: StateFlow<List<Expense>> = repository.getAllExpenses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todaysExpensesList: StateFlow<List<Expense>> = repository.getExpensesForDate(todayString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeLoans: StateFlow<List<Loan>> = repository.getActiveLoans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeLendings: StateFlow<List<Lending>> = repository.getActiveLendings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val debtStats: StateFlow<DebtStats> = repository.getDebtStats()
        .map { it ?: DebtStats(0.0, 0) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DebtStats(0.0, 0))

    val lendingStats: StateFlow<LendingStats> = repository.getLendingStats()
        .map { it ?: LendingStats(0.0, 0) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LendingStats(0.0, 0))

    val averageSpend: StateFlow<Double> = monthlySpend
        .map { it / 30.0 } // default /30 as per request
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun updateSettings(limit: Double, startDay: Int) {
        settingsManager.setDailyLimit(limit)
        settingsManager.setMonthStartDay(startDay)
        _dailyLimit.value = limit
        _monthStartDay.value = startDay
        showMessage("Settings saved")
    }

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

    fun updateExpense(expense: Expense) = viewModelScope.launch { repository.updateExpense(expense) }
    fun deleteExpense(expense: Expense) = viewModelScope.launch { repository.deleteExpense(expense) }

    fun addLoan(name: String, amount: String) {
        val amt = amount.toDoubleOrNull()
        if (name.isBlank() || amt == null) return
        viewModelScope.launch {
            repository.insertLoan(Loan(name = name, amount = amt))
            showMessage("Debt logged")
        }
    }
    
    fun updateLoan(loan: Loan) = viewModelScope.launch { repository.updateLoan(loan) }
    fun deleteLoan(loan: Loan) = viewModelScope.launch { repository.deleteLoan(loan) }

    fun payLoan(loanId: String, amount: String) {
        val lId = loanId.toIntOrNull()
        val amt = amount.toDoubleOrNull()
        if (lId == null || amt == null) return
        viewModelScope.launch {
            if (repository.recordRepayment(lId, amt)) showMessage("Repayment recorded")
        }
    }

    fun addLending(name: String, amount: String, mobileNumber: String, customDate: String) {
        val amt = amount.toDoubleOrNull()
        if (name.isBlank() || amt == null) return
        val dateToUse = if (customDate.isBlank()) todayString else customDate
        viewModelScope.launch {
            repository.insertLending(Lending(name = name, amount = amt, mobileNumber = mobileNumber, date = dateToUse))
            showMessage("Lending record added")
        }
    }

    fun updateLending(lending: Lending) = viewModelScope.launch { repository.updateLending(lending) }
    fun deleteLending(lending: Lending) = viewModelScope.launch { repository.deleteLending(lending) }

    fun receiveLending(lendingId: String, amount: String) {
        val lId = lendingId.toIntOrNull()
        val amt = amount.toDoubleOrNull()
        if (lId == null || amt == null) return
        viewModelScope.launch {
            if (repository.recordLendingReceived(lId, amt)) showMessage("Payment received")
        }
    }

    private fun showMessage(msg: String) {
        viewModelScope.launch { _snackbarMessage.emit(msg) }
    }
}
