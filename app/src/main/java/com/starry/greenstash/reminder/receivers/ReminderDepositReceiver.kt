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


package com.starry.greenstash.reminder.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.starry.greenstash.database.core.GoalWithTransactions
import com.starry.greenstash.database.goal.GoalDao
import com.starry.greenstash.database.transaction.Transaction
import com.starry.greenstash.database.transaction.TransactionDao
import com.starry.greenstash.database.transaction.TransactionType
import com.starry.greenstash.reminder.ReminderManager
import com.starry.greenstash.reminder.ReminderNotificationSender
import com.starry.greenstash.utils.NumberUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class ReminderDepositReceiver : BroadcastReceiver() {

    companion object {
        const val REMINDER_GOAL_ID = "reminder_deposit_goal_id"
        const val REMINDER_DEPOSIT_AMOUNT = "reminder_deposit_amount"
    }

    @Inject
    lateinit var goalDao: GoalDao

    @Inject
    lateinit var transactionDao: TransactionDao

    @Inject
    lateinit var reminderManager: ReminderManager

    @Inject
    lateinit var reminderNotificationSender: ReminderNotificationSender

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("ReminderDepositReceiver", "Received deposit action")
        val coroutineScope = CoroutineScope(Dispatchers.IO)

        coroutineScope.launch {
            val goalItem: GoalWithTransactions? = goalDao.getGoalWithTransactionById(
                intent.getLongExtra(REMINDER_GOAL_ID, 0L)
            )
            goalItem?.let {
                val defaultDepositValue = 0
                val depositAmount =
                    intent.getDoubleExtra(
                        REMINDER_DEPOSIT_AMOUNT,
                        defaultDepositValue.toDouble()
                    )
                if (depositAmount > 0) {
                    transactionDao.insertTransaction(
                        Transaction(
                            ownerGoalId = it.goal.goalId,
                            type = TransactionType.Deposit,
                            timeStamp = System.currentTimeMillis(),
                            amount = NumberUtils.roundDecimal(depositAmount),
                            notes = ""
                        )
                    )
                    reminderNotificationSender.updateWithDepositNotification(
                        it.goal.goalId,
                        depositAmount
                    )
                }
            }
        }

    }
}