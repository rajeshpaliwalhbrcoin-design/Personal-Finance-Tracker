package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.data.entities.Loan
import kotlinx.coroutines.flow.Flow

data class DebtStats(val totalDebt: Double, val activeLoansCount: Int)

@Dao
interface LoanDao {
    @Insert
    suspend fun insertLoan(loan: Loan)

    @Update
    suspend fun updateLoan(loan: Loan)

    @Delete
    suspend fun deleteLoan(loan: Loan)

    @Query("SELECT * FROM loans WHERE amount > paid ORDER BY id DESC")
    fun getActiveLoans(): Flow<List<Loan>>

    @Query("SELECT * FROM loans WHERE id = :id")
    suspend fun getLoanById(id: Int): Loan?

    @Query("SELECT COALESCE(SUM(amount - paid), 0.0) as totalDebt, COUNT(id) as activeLoansCount FROM loans WHERE amount > paid")
    fun getDebtStats(): Flow<DebtStats?>
}
