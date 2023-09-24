package com.starry.greenstash.backup

import com.google.gson.Gson
import com.starry.greenstash.database.goal.GoalDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackupManager(private val goalDao: GoalDao) {

    private val gsonInstance = Gson()
    suspend fun createDatabaseBackup() = withContext(Dispatchers.IO) {
        val goalsWithTransactions = goalDao.getAllGoals()

    }

    suspend fun restoreDatabaseBackup(): Nothing = withContext(Dispatchers.IO) {
        TODO()
    }
}