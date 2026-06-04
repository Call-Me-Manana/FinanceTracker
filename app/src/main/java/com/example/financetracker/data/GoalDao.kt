package com.example.financetracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals ORDER BY createdAt DESC")
    fun getAll(): Flow<List<Goal>>

    @Insert
    suspend fun insert(goal: Goal)

    @Delete
    suspend fun delete(goal: Goal)

    @Update
    suspend fun update(goal: Goal)

    @Query(
        """
        UPDATE goals
        SET currentAmount = CASE
            WHEN currentAmount + :amount > targetAmount THEN targetAmount
            ELSE currentAmount + :amount
        END
        WHERE id = :id
        """
    )
    suspend fun addMoney(id: Int, amount: Double)
}
