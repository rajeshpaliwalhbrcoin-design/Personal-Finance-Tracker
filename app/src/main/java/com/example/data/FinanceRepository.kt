package com.example.data

import com.example.data.dao.DailyExpense
import com.example.data.dao.DebtStats
import com.example.data.entities.Expense
import com.example.data.entities.Loan
import kotlinx.coroutines.flow.Flow

class FinanceRepository(private val db: FinanceDatabase) {
    private val expenseDao = db.expenseDao()
    private val loanDao = db.loanDao()

    fun getExpensesForDate(date: String): Flow<List<Expense>> = expenseDao.getExpensesForDate(date)

    fun getTodaySpend(today: String): Flow<Double?> = expenseDao.getTodaySpend(today)

    fun getMonthSpend(month: String): Flow<Double?> = expenseDao.getMonthSpend(month)

    fun getDailyExpenses(): Flow<List<DailyExpense>> = expenseDao.getDailyExpenses()

    suspend fun insertExpense(expense: Expense) = expenseDao.insertExpense(expense)

    fun getActiveLoans(): Flow<List<Loan>> = loanDao.getActiveLoans()

    fun getDebtStats(): Flow<DebtStats?> = loanDao.getDebtStats()

    suspend fun insertLoan(loan: Loan) = loanDao.insertLoan(loan)

    suspend fun recordRepayment(loanId: Int, amountReturned: Double): Boolean {
        val loan = loanDao.getLoanById(loanId) ?: return false
        val newPaid = minOf(loan.amount, loan.paid + amountReturned)
        loanDao.updateLoan(loan.copy(paid = newPaid))
        return true
    }
}
