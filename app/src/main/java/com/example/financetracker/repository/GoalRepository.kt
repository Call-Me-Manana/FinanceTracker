package com.example.financetracker.repository

import com.example.financetracker.data.Goal
import com.example.financetracker.data.GoalDao
import kotlinx.coroutines.flow.Flow

class GoalRepository(
    private val goalDao: GoalDao
) {

    fun getAllGoals(): Flow<List<Goal>> {
        return goalDao.getAll()
    }

    suspend fun insertGoal(
        goal: Goal
    ) {
        goalDao.insert(goal)
    }

    suspend fun addMoney(id: Int, amount: Double) {
        goalDao.addMoney(id, amount)
    }

    suspend fun delete(goal: Goal) {
        goalDao.delete(goal)
    }

    suspend fun update(goal: Goal) {
        goalDao.update(goal)
    }
}
