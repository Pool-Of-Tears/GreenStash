package com.starry.greenstash.ui.screens.dwscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.database.transaction.Transaction
import com.starry.greenstash.database.transaction.TransactionDao
import com.starry.greenstash.database.transaction.TransactionType
import com.starry.greenstash.ui.screens.settings.DateStyle
import com.starry.greenstash.utils.NumberUtils
import com.starry.greenstash.utils.PreferenceUtil
import com.starry.greenstash.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

data class DWScreenState(
    val amount: String = "",
    val notes: String = "",
)

@HiltViewModel
class DWViewModel @Inject constructor(
    private val goalDao: GoalDao,
    private val transactionDao: TransactionDao,
    private val preferenceUtil: PreferenceUtil
) : ViewModel() {

    var state by mutableStateOf(DWScreenState())

    fun getDateStyle(): DateStyle {
        val dateStyleValue = preferenceUtil.getString(
            PreferenceUtil.DATE_FORMAT_STR,
            DateStyle.DateMonthYear.pattern
        )
        return if (dateStyleValue == DateStyle.DateMonthYear.pattern) {
            DateStyle.DateMonthYear
        } else {
            DateStyle.YearMonthDate
        }
    }

    fun convertTransactionType(type: String): TransactionType {
        return when (type) {
            TransactionType.Deposit.name -> TransactionType.Deposit
            TransactionType.Withdraw.name -> TransactionType.Withdraw
            else -> throw IllegalArgumentException("Invalid transaction type")
        }
    }

    fun deposit(
        goalId: Long,
        dateTime: LocalDateTime,
        onGoalAchieved: () -> Unit,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val goal = getGoalById(goalId)!!
            addTransaction(
                goalId = goal.goalId,
                amount = amountToDouble(state.amount),
                notes = state.notes,
                dateTime = dateTime,
                transactionType = TransactionType.Deposit
            )
            /**
             * check weather goal is achieved or not after inserting the
             * amount in goal database and call the goal achieved function
             * to show a congratulations message to the user.
             */
            val goalItem = goalDao.getGoalWithTransactionById(goal.goalId)!!
            val remainingAmount = (goal.targetAmount - goalItem.getCurrentlySavedAmount())
            if (remainingAmount <= 0f) {
                withContext(Dispatchers.Main) { onGoalAchieved() }
            } else {
                withContext(Dispatchers.Main) { onComplete() }
            }
        }
    }

    fun withdraw(
        goalId: Long,
        dateTime: LocalDateTime,
        onWithDrawOverflow: () -> Unit,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val goal = getGoalById(goalId)!!
            val goalItem = goalDao.getGoalWithTransactionById(goal.goalId)!!
            val amount = amountToDouble(state.amount)

            if (amount > goalItem.getCurrentlySavedAmount()) {
                withContext(Dispatchers.Main) { onWithDrawOverflow() }
                return@launch
            }

            addTransaction(
                goalId = goal.goalId,
                amount = amount,
                notes = state.notes,
                dateTime = dateTime,
                transactionType = TransactionType.Withdraw
            )

            withContext(Dispatchers.Main) { onComplete() }
        }
    }

    private fun amountToDouble(amount: String) = NumberUtils.roundDecimal(amount.toDouble())

    private suspend fun getGoalById(goalId: Long) = goalDao.getGoalById(goalId)

    private suspend fun addTransaction(
        goalId: Long,
        amount: Double,
        notes: String,
        dateTime: LocalDateTime,
        transactionType: TransactionType
    ) {
        val timeStamp = Utils.getEpochTime(dateTime)

        val transaction = Transaction(
            ownerGoalId = goalId,
            type = transactionType,
            timeStamp = timeStamp,
            amount = amount,
            notes = notes
        )
        transactionDao.insertTransaction(transaction)
    }
}