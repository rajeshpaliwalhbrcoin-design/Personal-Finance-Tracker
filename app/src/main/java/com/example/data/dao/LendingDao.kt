package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.data.entities.Lending
import kotlinx.coroutines.flow.Flow

data class LendingStats(val totalLentStr: Double, val activeLendingsCount: Int)

@Dao
interface LendingDao {
    @Insert
    suspend fun insertLending(lending: Lending)

    @Update
    suspend fun updateLending(lending: Lending)

    @Delete
    suspend fun deleteLending(lending: Lending)

    @Query("SELECT * FROM lendings WHERE amount > received ORDER BY id DESC")
    fun getActiveLendings(): Flow<List<Lending>>

    @Query("SELECT * FROM lendings WHERE id = :id")
    suspend fun getLendingById(id: Int): Lending?

    @Query("SELECT COALESCE(SUM(amount - received), 0.0) as totalLentStr, COUNT(id) as activeLendingsCount FROM lendings WHERE amount > received")
    fun getLendingStats(): Flow<LendingStats?>
}
