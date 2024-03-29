/**
 * MIT License
 *
 * Copyright (c) [2022 - Present] Stɑrry Shivɑm
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.starry.greenstash.ui.screens.info.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.database.transaction.Transaction
import com.starry.greenstash.database.transaction.TransactionDao
import com.starry.greenstash.database.transaction.TransactionType
import com.starry.greenstash.ui.screens.settings.viewmodels.DateStyle
import com.starry.greenstash.utils.GoalTextUtils
import com.starry.greenstash.utils.PreferenceUtil
import com.starry.greenstash.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

data class InfoScreenState(
    val goalData: Flow<GoalWithTransactions?>? = null
)

data class EditGoalState(
    val amount: String = "",
    val notes: String = "",
)

@HiltViewModel
class InfoViewModel @Inject constructor(
    private val goalDao: GoalDao,
    private val transactionDao: TransactionDao,
    private val preferenceUtil: PreferenceUtil
) : ViewModel() {

    val goalTextUtils = GoalTextUtils(preferenceUtil)
    var state by mutableStateOf(InfoScreenState())
    var editGoalState by mutableStateOf(EditGoalState())

    fun loadGoalData(goalId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val goalWithTransactions = goalDao.getGoalWithTransactionByIdAsFlow(goalId)
            delay(450L)
            state = state.copy(goalData = goalWithTransactions)
        }
    }

    fun setEditAmountAndNotes(transaction: Transaction) {
        editGoalState = EditGoalState(
            amount = transaction.amount.toString(),
            notes = transaction.notes,
        )
    }

    fun getDateStyleValue() = preferenceUtil.getString(
        PreferenceUtil.DATE_FORMAT_STR, DateStyle.DateMonthYear.pattern
    )

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionDao.deleteTransaction(transaction)
        }
    }

    fun updateTransaction(
        transaction: Transaction,
        transactionTime: LocalDateTime,
        transactionType: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val newTransaction = transaction.copy(
                type = TransactionType.valueOf(transactionType),
                timeStamp = Utils.getEpochTime(transactionTime),
                amount = Utils.roundDecimal(editGoalState.amount.toDouble()),
                notes = editGoalState.notes
            )
            newTransaction.transactionId = transaction.transactionId
            transactionDao.updateTransaction(newTransaction)
        }
    }

    fun getDefaultCurrencyValue() = preferenceUtil.getString(
        PreferenceUtil.DEFAULT_CURRENCY_STR, "$"
    )!!
}