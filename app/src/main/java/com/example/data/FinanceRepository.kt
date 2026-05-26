package com.example.data

import com.example.data.dao.DailyExpense
import com.example.data.dao.DebtStats
import com.example.data.dao.LendingStats
import com.example.data.entities.Expense
import com.example.data.entities.Lending
import com.example.data.entities.Loan
import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val db: FinanceDatabase) {
    private val expenseDao = db.expenseDao()
    private val loanDao = db.loanDao()
    private val lendingDao = db.lendingDao()

    fun getExpensesForDate(date: String): Flow<List<Expense>> = expenseDao.getExpensesForDate(date)

    fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses()

    fun getTodaySpend(today: String): Flow<Double?> = expenseDao.getTodaySpend(today)

    fun getSpendBetweenDates(startDate: String, endDate: String): Flow<Double?> = expenseDao.getSpendBetweenDates(startDate, endDate)

    fun getDailyExpenses(): Flow<List<DailyExpense>> = expenseDao.getDailyExpenses()

    suspend fun insertExpense(expense: Expense) = expenseDao.insertExpense(expense)
    suspend fun updateExpense(expense: Expense) = expenseDao.updateExpense(expense)
    suspend fun deleteExpense(expense: Expense) = expenseDao.deleteExpense(expense)

    fun getActiveLoans(): Flow<List<Loan>> = loanDao.getActiveLoans()

    fun getDebtStats(): Flow<DebtStats?> = loanDao.getDebtStats()

    suspend fun insertLoan(loan: Loan) = loanDao.insertLoan(loan)
    suspend fun updateLoan(loan: Loan) = loanDao.updateLoan(loan)
    suspend fun deleteLoan(loan: Loan) = loanDao.deleteLoan(loan)

    suspend fun recordRepayment(loanId: Int, amountReturned: Double): Boolean {
        val loan = loanDao.getLoanById(loanId) ?: return false
        val newPaid = minOf(loan.amount, loan.paid + amountReturned)
        loanDao.updateLoan(loan.copy(paid = newPaid))
        return true
    }

    fun getActiveLendings(): Flow<List<Lending>> = lendingDao.getActiveLendings()

    fun getLendingStats(): Flow<LendingStats?> = lendingDao.getLendingStats()

    suspend fun insertLending(lending: Lending) = lendingDao.insertLending(lending)
    suspend fun updateLending(lending: Lending) = lendingDao.updateLending(lending)
    suspend fun deleteLending(lending: Lending) = lendingDao.deleteLending(lending)

    suspend fun recordLendingReceived(lendingId: Int, amountReceived: Double): Boolean {
        val lending = lendingDao.getLendingById(lendingId) ?: return false
        val newReceived = minOf(lending.amount, lending.received + amountReceived)
        lendingDao.updateLending(lending.copy(received = newReceived))
        return true
    }
}

