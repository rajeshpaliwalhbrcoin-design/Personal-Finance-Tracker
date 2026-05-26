package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.data.entities.Expense
import kotlinx.coroutines.flow.Flow

data class DailyExpense(val date: String, val totalAmount: Double)

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insertExpense(expense: Expense)

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC, id DESC")
    fun getAllExpenses(): Flow<List<Expense>>
    
    @Query("SELECT * FROM expenses WHERE date = :date ORDER BY id DESC LIMIT 15")
    fun getExpensesForDate(date: String): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date = :today")
    fun getTodaySpend(today: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE date >= :startDate AND date <= :endDate")
    fun getSpendBetweenDates(startDate: String, endDate: String): Flow<Double?>

    @Query("SELECT date, SUM(amount) as totalAmount FROM expenses GROUP BY date ORDER BY date DESC")
    fun getDailyExpenses(): Flow<List<DailyExpense>>
}
